package nl.bumastemra.portal.libraries.auth

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
import io.ktor.util.date.GMTDate
import io.ktor.util.date.plus

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
    with (application) {
        val config: FusionAuthConfig = FusionAuthConfig.fromApplicationConfig(environment.config)

        install(Authentication) {
            oauth(FUSION_AUTH_PROVIDER) {
                client = config.httpClient
                urlProvider = { config.callBackUrl }
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
                        call.setCookie("access_token", currentPrincipal.accessToken)
                        if (currentPrincipal.refreshToken != null) {
                            call.setCookie("refresh_token", currentPrincipal.refreshToken!!)
                        }
                    }
                    call.respondRedirect("/")
                }
            }
        }
    }
}

private fun ApplicationCall.setCookie(name: String, value: String) {
    val host = request.host()
    val expires = GMTDate().plus(365 * 24 * 60 * 60 * 1000)
    response.cookies.append(name, value, domain = host, path = "/", secure = false, expires = expires, httpOnly = true)
}