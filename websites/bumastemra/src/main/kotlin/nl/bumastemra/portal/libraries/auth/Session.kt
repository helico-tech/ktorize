@file:OptIn(ExperimentalUuidApi::class)

package nl.bumastemra.portal.libraries.auth

import io.ktor.server.application.ApplicationCall
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi

data class SessionConfig(
    val cookieName: String,
    val cookieAgeInSeconds: Long,
    val signKey: String,
) {
    companion object {
        fun fromApplicationConfig(applicationConfig: ApplicationConfig): SessionConfig {
            return SessionConfig(
                cookieName = applicationConfig.property("session.cookieName").getString(),
                cookieAgeInSeconds = applicationConfig.property("session.cookieAgeInSeconds").getString().toLong(),
                signKey = applicationConfig.property("session.signKey").getString(),
            )
        }
    }
}

@Serializable
data class UserSession(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val redirectUrl: String? = null,
    val profile: Profile? = null
)

@Serializable
data class Profile(
    val id: String,
    val role: String
)

val ApplicationCall.userSession: UserSession get() {
    val userSession = sessions.get<UserSession>()
    return userSession ?: UserSession(null, null)
}

fun ApplicationCall.updateUserSession(block: UserSession.() -> UserSession) {
    val updatedUserSession = userSession.block()
    sessions.set(updatedUserSession)
}