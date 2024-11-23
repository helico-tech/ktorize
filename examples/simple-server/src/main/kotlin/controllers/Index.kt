package controllers

import io.ktor.server.routing.*
import layouts.BaseLayout
import nl.helico.ktorize.html.respondLayout

fun Routing.index() {
    get("/") {
        call.respondLayout(factory = BaseLayout) {
            title = "Home"
        }
    }
}