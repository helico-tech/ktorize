package nl.bumastemra.portal.libraries.auth

import io.ktor.http.parameters
import io.ktor.http.path
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.uri
import io.ktor.server.response.respondRedirect
import nl.helico.ktorize.guards.Guard
import nl.helico.ktorize.guards.OnUnauthorized

class AuthenticatedGuard(
    override val onUnauthorized: OnUnauthorized? = null
) : Guard {
    override fun isAuthorized(call: ApplicationCall): Guard.AuthorizationResult {
        return if (call.accessToken != null) {
            Guard.AuthorizationResult.Success
        } else {
            Guard.AuthorizationResult.Unauthorized("No access token found")
        }
    }
}

class RolesGuard(
    private val roles: List<String>,
    override val onUnauthorized: OnUnauthorized? = null
) : Guard {
    override fun isAuthorized(call: ApplicationCall): Guard.AuthorizationResult {
        val user = call.user ?: return Guard.AuthorizationResult.Unauthorized("No user found")
        if (roles.all { it in user.roles }) {
            return Guard.AuthorizationResult.Success
        }
        return Guard.AuthorizationResult.Unauthorized("User does not have the required roles: ${roles.joinToString()}")
    }
}

val authenticated: AuthenticatedGuard get() {
    val onUnauthorized: OnUnauthorized = { call, _ ->
        call.updateUserSession { copy(redirectUrl = call.request.uri) }
        call.respondRedirect(call.application.attributes[LoginUrlKey], permanent = false)
    }

    return AuthenticatedGuard(onUnauthorized)
}

fun roles(vararg roles: String, onUnauthorized: OnUnauthorized? = null): RolesGuard {
    return RolesGuard(roles.toList(), onUnauthorized)
}