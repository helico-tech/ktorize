package nl.helico.ktorize.guards

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respondText

interface Guard {

    val onUnauthorized: OnUnauthorized?

    fun authorize(call: ApplicationCall): AuthorizationResult

    sealed interface AuthorizationResult {
        object Success : AuthorizationResult
        data class Unauthorized(val message: String, val statusCode: HttpStatusCode = HttpStatusCode.Forbidden) : AuthorizationResult
    }
}