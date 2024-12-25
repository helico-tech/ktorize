package nl.bumastemra.portal.routes

import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import nl.bumastemra.portal.views.WorksPage
import nl.helico.ktorize.html.respondHtml

fun Routing.works() {
    get("/works") {
        call.respondHtml(view = WorksPage())
    }
}