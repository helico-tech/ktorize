package nl.bumastemra.portal.libraries.auth

import com.auth0.jwk.JwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.http.parameters
import io.ktor.server.application.ApplicationCall
import io.ktor.util.AttributeKey
import io.ktor.util.logging.KtorSimpleLogger
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import java.security.interfaces.RSAPublicKey
import java.time.Instant
import kotlin.text.isNullOrEmpty

sealed interface AccessToken {

    sealed interface Invalid : AccessToken

    data object Missing : AccessToken
    data class Error(val error: String) : Invalid
    data object Expired : Invalid

    sealed interface Valid : AccessToken {
        val jwt: DecodedJWT
        val refreshToken: String?
    }

    data class Refreshed(override val jwt: DecodedJWT, override val refreshToken: String?) : Valid
    data class Ok(override val jwt: DecodedJWT, override val refreshToken: String?) : Valid

    companion object {
        val Key = AttributeKey<Valid>("AccessToken")
    }
}

class AccessTokenHandler(
    val httpClient: HttpClient,
    val refreshTokenUrl: String,
    val clientId: String,
    val clientSecret: String,
    val jwkProvider: JwkProvider,
) {

    private val logger = KtorSimpleLogger("AccessTokenHandler")

    suspend fun getAccessToken(call: ApplicationCall): AccessToken {
        val accessToken = call.request.cookies["access_token"]
        if (accessToken.isNullOrEmpty()) return AccessToken.Missing

        val refreshToken = call.request.cookies["refresh_token"]

        return getAccessToken(accessToken, refreshToken, tryRefresh = true)
    }

    private suspend fun getAccessToken(accessToken: String, refreshToken: String?, tryRefresh: Boolean): AccessToken {
        var jwtResult = getAndValidateJWT(accessToken)

        if (jwtResult is ValidateJWTResult.Invalid) {
            return AccessToken.Error(jwtResult.error)
        }

        val jwt = (jwtResult as ValidateJWTResult.Valid).jwt
        if (jwt.expiresAtAsInstant.isBefore(Instant.now())) {
            if (!tryRefresh) return AccessToken.Expired
            return tryAccessTokenRefresh(refreshToken) ?: return AccessToken.Expired
        }

        return AccessToken.Ok(jwt, refreshToken)
    }

    private fun getAndValidateJWT(jwt: String): ValidateJWTResult {
        try {
            val jwt = JWT.decode(jwt)
            val jwk = jwkProvider.get(jwt.keyId)
            val key = jwk.publicKey as RSAPublicKey
            val algorithm = Algorithm.RSA256(key, null)
            algorithm.verify(jwt)

            return ValidateJWTResult.Valid(jwt)
        } catch (e: Exception) {
            return ValidateJWTResult.Invalid(e.message ?: "Unknown error")
        }
    }


    private suspend fun tryAccessTokenRefresh(refreshToken: String?): AccessToken? {
        this.logger.trace("Access token was expired, trying to refresh")
        if (refreshToken.isNullOrEmpty()) return null

        try {
            val response = httpClient.submitForm(
                url = refreshTokenUrl,
                formParameters = parameters {
                    append("grant_type", "refresh_token")
                    append("refresh_token", refreshToken)
                    append("client_id", clientId)
                    append("client_secret", clientSecret)
                }
            )

            val data = response.body<JsonObject>()

            val accessToken = data["access_token"]?.jsonPrimitive?.contentOrNull
            val refreshToken = data["refresh_token"]?.jsonPrimitive?.contentOrNull

            if (accessToken == null) return AccessToken.Error("No access token in refresh token response")

            val validated = getAccessToken(accessToken, refreshToken, tryRefresh = false)

            return when (validated) {
                is AccessToken.Valid -> AccessToken.Refreshed(validated.jwt, refreshToken)
                is AccessToken.Invalid -> AccessToken.Error("Invalid access token after refresh")
                else -> AccessToken.Error("Unknown error")
            }
        } catch (e: Exception) {
            return AccessToken.Error(e.message ?: "Unknown error")
        }

        return null
    }

    sealed interface ValidateJWTResult {
        data class Valid(val jwt: DecodedJWT) : ValidateJWTResult
        data class Invalid(val error: String) : ValidateJWTResult
    }
}

