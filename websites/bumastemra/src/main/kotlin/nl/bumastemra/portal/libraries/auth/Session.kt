package nl.bumastemra.portal.libraries.auth

import io.ktor.server.application.ApplicationCall
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import kotlinx.serialization.Serializable

@Serializable
data class UserSession(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val redirectUrl: String? = null,
)

val ApplicationCall.userSession: UserSession get() {
    val userSession = sessions.get<UserSession>()
    return userSession ?: UserSession(null, null)
}

fun ApplicationCall.updateUserSession(block: UserSession.() -> UserSession) {
    val updatedUserSession = userSession.block()
    sessions.set(updatedUserSession)
}