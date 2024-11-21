package nl.bumastemra.portal.features.dashboard

import io.ktor.server.application.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.*
import nl.bumastemra.portal.libraries.auth.authGuard
import nl.bumastemra.portal.libraries.auth.user
import nl.helico.ktorize.html.respondHtml

fun Application.routes() {

    routing {

        get("/") {
            call.respondHtml(view = DashboardView(call.user))
        }

        authGuard("member") {
            get("/protected") {
                call.respondText("Protected")
            }
        }
    }
}