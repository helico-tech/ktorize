@file:OptIn(ExperimentalUuidApi::class)

package nl.bumastemra.portal.libraries.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.log
import io.ktor.util.AttributeKey
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class User(
    val id: Uuid,
    val email: String,
    val name: String,
    val roles: List<String> = emptyList()
) {
    companion object {
        val Key = AttributeKey<User>("User")

        fun fromDecodedJWT(jwt: DecodedJWT): User {
            return User(
                id = Uuid.parse(jwt.getClaim("sub").asString()),
                email = jwt.getClaim("email").asString(),
                name = jwt.getClaim("fullName").asString(),
                roles = jwt.getClaim("roles").asList(String::class.java)
            )
        }
    }
}

val ApplicationCall.user: User? get() {
    return try {
        this.attributes.getOrNull(User.Key)
    } catch (e: Exception) {
        application.log.error("Failed to get user from call", e)
        null
    }
}