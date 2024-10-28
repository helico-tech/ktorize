package nl.bumastemra.portal.modules.auth

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.module() {
    routing {
        controllers()
    }
}