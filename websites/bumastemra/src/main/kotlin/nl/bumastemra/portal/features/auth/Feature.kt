package nl.bumastemra.portal.features.auth

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.authFeature() {
    routing {
        controllers()
    }
}