package nl.bumastemra.portal.server

import io.ktor.server.application.*
import io.ktor.server.routing.*
import nl.bumastemra.portal.features.auth.authFeature

fun Application.server() {
    routing {
        authFeature()
    }
}