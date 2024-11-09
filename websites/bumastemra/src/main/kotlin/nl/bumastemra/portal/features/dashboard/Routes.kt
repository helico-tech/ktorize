package nl.bumastemra.portal.features.dashboard

import io.ktor.server.application.*
import io.ktor.server.routing.*
import nl.helico.ktorize.html.respondHtml

fun Application.routes() {
    routing {
        get("/") {
            call.respondHtml(view = DashboardView)
        }
    }
}