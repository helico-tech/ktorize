package nl.helico.website

import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import nl.helico.ktorize.html.respondHtml

fun Application.routes() {
    routing {
        get("/") {
            call.respondHtml(view = Layout())
        }
    }
}