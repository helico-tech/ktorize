package nl.bumastemra.portal.routes

import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import nl.bumastemra.portal.libraries.auth.authenticated
import nl.bumastemra.portal.libraries.auth.hasProfile
import nl.bumastemra.portal.views.DashboardPage
import nl.helico.ktorize.guards.guard
import nl.helico.ktorize.html.respondHtml

fun Routing.dashboard() {
    guard(authenticated, hasProfile) {
        get("/dashboard") {
            call.respondHtml(view = DashboardPage())
        }
    }
}