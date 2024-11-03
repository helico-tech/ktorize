package nl.bumastemra.portal.features.auth

import io.ktor.server.application.*
import io.ktor.server.routing.*
import nl.helico.ktorize.html.respondHtml

fun Application.controllers() {
    routing {
        route("/auth") {
            get("") {
                call.respondHtml(view = LoginPage)
            }
        }
    }
}