package nl.bumastemra.portal.modules.landingpage

import io.ktor.server.application.*
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import nl.bumastemra.portal.libraries.auth.user
import nl.helico.ktorize.html.respondHtml

fun Application.landingPage() {
    routing {
        get("/") {
            call.respondHtml(view = LandingPageView(call.user))
        }
    }
}