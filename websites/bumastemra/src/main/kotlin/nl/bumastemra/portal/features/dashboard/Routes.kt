package nl.bumastemra.portal.features.dashboard

import io.ktor.server.application.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.*
import nl.bumastemra.portal.libraries.auth.roles
import nl.bumastemra.portal.libraries.auth.user
import nl.helico.ktorize.guards.guard
import nl.helico.ktorize.html.respondHtml

fun Application.routes() {

    routing {

        get("/") {
            call.respondHtml(view = DashboardView(call.user))
        }

        guard(roles("member")) {
            get("/protected") {
                call.respondText("Protected")
            }
        }
    }
}