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
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.date.*

const val FUSION_AUTH_PLUGIN = "FusionAuthPlugin"
const val FUSION_AUTH_PROVIDER = "fusionauth"

internal val DEFAULT_HTTP_CLIENT = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }
}

data class FusionAuthConfig(
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
        ): FusionAuthConfig {
            return FusionAuthConfig(
                httpClient = httpClient,
                baseUrl = applicationConfig.property("fusionauth.baseUrl").getString(),
                callBackUrl = applicationConfig.property("fusionauth.callBackUrl").getString(),
                clientId = applicationConfig.property("fusionauth.clientId").getString(),
                clientSecret = applicationConfig.property("fusionauth.clientSecret").getString(),
            )
        }
    }
}

val FusionAuthPlugin = createApplicationPlugin(FUSION_AUTH_PLUGIN) {

    val config: FusionAuthConfig = FusionAuthConfig.fromApplicationConfig(environment.config)

    val accessTokenHandler = AccessTokenHandler(
        httpClient = config.httpClient,
        refreshTokenUrl = "${config.baseUrl}/oauth2/token",
        jwkProvider = JwkProviderBuilder(config.baseUrl).cached(true).build(),
        clientId = config.clientId,
        clientSecret = config.clientSecret,
    )

    val redirectMap = mutableMapOf<String, String>()

    with(application) {
        install(Sessions) {
            cookie<UserSession>("user_session") {
                cookie.path = "/"
                cookie.httpOnly = true
                cookie.secure = true
                cookie.maxAgeInSeconds = 24 * 3600
            }
        }

        install(Authentication) {
            oauth(FUSION_AUTH_PROVIDER) {
                client = config.httpClient
                urlProvider = {
                    config.callBackUrl
                }
                providerLookup = {
                    OAuthServerSettings.OAuth2ServerSettings(
                        name = FUSION_AUTH_PROVIDER,
                        authorizeUrl = "${config.baseUrl}/oauth2/authorize",
                        accessTokenUrl = "${config.baseUrl}/oauth2/token",
                        requestMethod = HttpMethod.Post,
                        clientId = config.clientId,
                        clientSecret = config.clientSecret,
                        defaultScopes = listOf("offline_access"),
                    )
                }
            }
        }

        routing {
            authenticate(FUSION_AUTH_PROVIDER) {
                get("/auth/login") {}

                get("/auth/callback") {
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
            call.attributes.put(UserAttributeKey, user)
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