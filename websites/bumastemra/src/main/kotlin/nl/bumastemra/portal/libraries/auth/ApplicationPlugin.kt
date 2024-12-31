@file:OptIn(ExperimentalUuidApi::class)

package nl.bumastemra.portal.libraries.auth

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.config.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.SessionTransportTransformerMessageAuthentication
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.util.AttributeKey
import io.ktor.util.hex
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi

const val OAUTH_PLUGIN = "OAuthPlugin"
const val FUSION_AUTH_PROVIDER = "fusionauth"

internal val DEFAULT_HTTP_CLIENT = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
}

data class OAuthConfig(
    val httpClient: HttpClient,
    val baseUrl: String,
    val callBackUrl: String,
    val clientId: String,
    val clientSecret: String,
) {
    companion object {
        fun fromApplicationConfig(
            applicationConfig: ApplicationConfig,
            httpClient: HttpClient = DEFAULT_HTTP_CLIENT
        ): OAuthConfig {
            return OAuthConfig(
                httpClient = httpClient,
                baseUrl = applicationConfig.property("oauth.baseUrl").getString(),
                callBackUrl = applicationConfig.property("oauth.callBackUrl").getString(),
                clientId = applicationConfig.property("oauth.clientId").getString(),
                clientSecret = applicationConfig.property("oauth.clientSecret").getString(),
            )
        }
    }
}

val LoginUrlKey = AttributeKey<String>("LoginUrl")

val OAuthPlugin = createApplicationPlugin(OAUTH_PLUGIN) {

    val oauthConfig: OAuthConfig = OAuthConfig.fromApplicationConfig(environment.config)
    val sessionConfig: SessionConfig = SessionConfig.fromApplicationConfig(environment.config)

    val accessTokenHandler = AccessTokenHandler(
        httpClient = oauthConfig.httpClient,
        refreshTokenUrl = "${oauthConfig.baseUrl}/oauth2/token",
        jwkProvider = JwkProviderBuilder(oauthConfig.baseUrl).cached(true).build(),
        clientId = oauthConfig.clientId,
        clientSecret = oauthConfig.clientSecret,
    )

    with(application) {
        install(Sessions) {
            val secretSignKey = hex(sessionConfig.signKey)

            cookie<UserSession>(sessionConfig.cookieName) {
                cookie.path = "/"
                cookie.httpOnly = true
                cookie.secure = true
                cookie.maxAgeInSeconds = sessionConfig.cookieAgeInSeconds
                transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
            }
        }

        install(Authentication) {
            oauth(FUSION_AUTH_PROVIDER) {
                client = oauthConfig.httpClient
                urlProvider = {
                    oauthConfig.callBackUrl
                }
                providerLookup = {
                    OAuthServerSettings.OAuth2ServerSettings(
                        name = FUSION_AUTH_PROVIDER,
                        authorizeUrl = "${oauthConfig.baseUrl}/oauth2/authorize",
                        accessTokenUrl = "${oauthConfig.baseUrl}/oauth2/token",
                        requestMethod = HttpMethod.Post,
                        clientId = oauthConfig.clientId,
                        clientSecret = oauthConfig.clientSecret,
                        defaultScopes = listOf("offline_access"),
                    )
                }
            }
        }

        attributes.put(LoginUrlKey, "/auth/login")

        routing {
            authenticate(FUSION_AUTH_PROVIDER) {
                get("/auth/login") {}

                get("/login/check") {
                    val currentPrincipal: OAuthAccessTokenResponse.OAuth2? = call.authentication.principal()
                    if (currentPrincipal != null) {
                        call.setAccessToken(currentPrincipal.accessToken)
                        if (currentPrincipal.refreshToken != null) {
                            call.setRefreshToken(currentPrincipal.refreshToken!!)
                        }
                    }

                    val redirectUrl = call.userSession.redirectUrl

                    if (redirectUrl != null) {
                        call.updateUserSession { copy(redirectUrl = null) }
                        call.respondRedirect(redirectUrl)
                    } else {
                        call.respondRedirect("/")
                    }
                }
            }

            get("/auth/logout") {
                call.removeAccessToken()
                call.removeRefreshToken()
                call.respondRedirect("/")
            }
        }
    }

    onCall { call ->
        val token = accessTokenHandler.getAccessToken(call)

        if (token !is AccessToken.Valid) {
            if (token !is AccessToken.Missing) {
                call.application.log.warn("Invalid access token: $token")
            }
            call.removeAccessToken()
            call.removeRefreshToken()
            return@onCall
        }

        if (token is AccessToken.Refreshed) {
            call.setAccessToken(token.jwt.token)
            if (token.refreshToken != null) {
                call.setRefreshToken(token.refreshToken)
            }
        }

        call.attributes.put(AccessToken.Key, token)

        try {
            val user = User.fromDecodedJWT(token.jwt)
            call.attributes.put(User.Key, user)
        } catch (e: Exception) {
            call.application.log.error("Failed to get user from access token", e)
        }
    }
}

val ApplicationCall.accessToken
    get(): AccessToken.Valid? {
        return attributes.getOrNull(AccessToken.Key)
    }

private fun ApplicationCall.setAccessToken(token: String) {
    updateUserSession { copy(accessToken = token) }
}

private fun ApplicationCall.removeAccessToken() {
    updateUserSession { copy(accessToken = null) }
}

private fun ApplicationCall.setRefreshToken(token: String) {
    updateUserSession { copy(refreshToken = token) }
}

private fun ApplicationCall.removeRefreshToken() {
    updateUserSession { copy(refreshToken = null) }
}