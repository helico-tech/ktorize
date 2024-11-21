package nl.bumastemra.portal.libraries.auth

import io.ktor.server.application.ApplicationCall
import nl.helico.ktorize.guards.Guard
import nl.helico.ktorize.guards.OnUnauthorized

class RolesGuard(
    private val roles: List<String>,
    override val onUnauthorized: OnUnauthorized? = null
) : Guard {
    override fun authorize(call: ApplicationCall): Guard.AuthorizationResult {
        val user = call.user ?: return Guard.AuthorizationResult.Unauthorized("No user found")
        if (roles.all { it in user.roles }) {
            return Guard.AuthorizationResult.Success
        }
        return Guard.AuthorizationResult.Unauthorized("User does not have the required roles")
    }
}

fun roles(vararg roles: String, onUnauthorized: OnUnauthorized? = null): RolesGuard {
    return RolesGuard(roles.toList(), onUnauthorized)
}