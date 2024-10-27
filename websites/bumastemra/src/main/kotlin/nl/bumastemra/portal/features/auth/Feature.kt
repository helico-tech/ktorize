package nl.bumastemra.portal.features.auth

import io.ktor.server.routing.*
import kotlinx.html.body
import nl.helico.ktorize.html.respondHtml

fun Routing.authFeature(root: String = "/") {

    get("$root/login") {
        call.respondHtml {
            body {
                +"Login"
            }
        }
    }

}