package nl.bumastemra.portal.libraries.auth

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.request.host
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.AttributeKey
import io.ktor.util.date.GMTDate
import io.ktor.util.date.plus
import kotlin.text.isNullOrEmpty

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
        fun fromApplicationConfig(applicationConfig: ApplicationConfig, httpClient: HttpClient = DEFAULT_HTTP_CLIENT): FusionAuthConfig {
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

    with (application) {
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
                        call.setAccessTokenCookie(currentPrincipal.accessToken)
                        if (currentPrincipal.refreshToken != null) {
                            call.setRefreshTokenCookie(currentPrincipal.refreshToken!!)
                        }
                    }
                    call.respondRedirect("/")
                }
            }
        }
    }

    onCall { call ->
        val token = accessTokenHandler.getAccessToken(call)

        if (token !is AccessToken.Valid) {
            call.application.log.warn("Invalid access token: $token")
            return@onCall
        }

        if (token is AccessToken.Refreshed) {
            call.setAccessTokenCookie(token.jwt.token)
            if (token.refreshToken != null) {
                call.setRefreshTokenCookie(token.refreshToken)
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

private fun ApplicationCall.setAccessTokenCookie(token: String) {
    setCookie("access_token", token,  24 * 3600)
}

private fun ApplicationCall.setRefreshTokenCookie(token: String) {
    setCookie("refresh_token", token, 24 * 3600)
}

private fun ApplicationCall.setCookie(name: String, value: String, durationInSeconds: Int) {
    val host = request.host()
    val expires = GMTDate().plus(durationInSeconds * 1000L)
    response.cookies.append(name, value, domain = host, path = "/", secure = false, expires = expires, httpOnly = true)
}