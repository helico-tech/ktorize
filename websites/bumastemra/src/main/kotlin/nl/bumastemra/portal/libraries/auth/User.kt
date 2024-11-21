package nl.bumastemra.portal.libraries.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.log
import io.ktor.util.AttributeKey

val UserAttributeKey = AttributeKey<User>("User")

data class User(
    val id: String,
    val email: String,
    val name: String,
    val roles: List<String> = emptyList()
) {
    companion object {
        fun fromJWT(jwt: String): User {
            val jwt = JWT.decode(jwt)
            return fromDecodedJWT(jwt)
        }

        fun fromDecodedJWT(jwt: DecodedJWT): User {
            return User(
                id = jwt.getClaim("sub").asString(),
                email = jwt.getClaim("email").asString(),
                name = jwt.getClaim("fullName").asString(),
                roles = jwt.getClaim("roles").asList(String::class.java)
            )
        }
    }
}

val ApplicationCall.user: User? get() {
    return try {
        this.attributes.getOrNull(UserAttributeKey)
    } catch (e: Exception) {
        application.log.error("Failed to get user from call", e)
        null
    }
}