package nl.helico.ktorize.guards

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import nl.helico.ktorize.guards.Guard.AuthorizationResult

interface Guard {

    val onUnauthorized: OnUnauthorized?

    fun isAuthorized(call: ApplicationCall): AuthorizationResult

    sealed interface AuthorizationResult {
        object Success : AuthorizationResult
        data class Unauthorized(val message: String, val statusCode: HttpStatusCode = HttpStatusCode.Forbidden) : AuthorizationResult
    }
}

fun allOf(vararg guards: Guard): Guard {
    return object : Guard {
        override val onUnauthorized: OnUnauthorized? = null
        override fun isAuthorized(call: ApplicationCall): AuthorizationResult {
            if (guards.all { it.isAuthorized(call) is AuthorizationResult.Success }) {
                return AuthorizationResult.Success
            } else {
                return AuthorizationResult.Unauthorized("Unauthorized", HttpStatusCode.Forbidden)
            }
        }
    }
}

fun anyOf(vararg guards: Guard): Guard {
    return object : Guard {
        override val onUnauthorized: OnUnauthorized? = null
        override fun isAuthorized(call: ApplicationCall): AuthorizationResult {
            if (guards.any { it.isAuthorized(call) is AuthorizationResult.Success }) {
                return AuthorizationResult.Success
            } else {
                return AuthorizationResult.Unauthorized("Unauthorized", HttpStatusCode.Forbidden)
            }
        }
    }
}

fun and(left: Guard, right: Guard): Guard {
    return allOf(left, right)
}

fun or(left: Guard, right: Guard): Guard {
    return anyOf(left, right)
}