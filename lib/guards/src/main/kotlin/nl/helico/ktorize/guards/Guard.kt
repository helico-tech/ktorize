package nl.helico.ktorize.guards

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall

interface Guard {

    val onUnauthorized: OnUnauthorized?

    fun isAuthorized(call: ApplicationCall): AuthorizationResult

    sealed interface AuthorizationResult {
        object Success : AuthorizationResult
        data class Unauthorized(val message: String, val statusCode: HttpStatusCode = HttpStatusCode.Forbidden) : AuthorizationResult
    }
}