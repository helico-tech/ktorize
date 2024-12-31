package nl.bumastemra.portal.routes

import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.routes() {
    routing {
        index()
        dashboard()
        works()
    }
}