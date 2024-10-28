package nl.bumastemra.portal.features.auth

import io.ktor.server.routing.*
import nl.helico.ktorize.html.respondHtml

fun Routing.controllers() {
    route("/auth") {
        get("/") {
            call.respondHtml(view = LoginPage)
        }
    }
}