package nl.bumastemra.portal.features.auth

import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.html.h1
import nl.bumastemra.portal.features.shared.components.CenterContainer
import nl.bumastemra.portal.features.shared.components.FullPageContainer
import nl.bumastemra.portal.features.shared.components.Panel
import nl.bumastemra.portal.features.shared.layouts.BaseLayout
import nl.helico.ktorize.html.respondHtml

fun Application.controllers() {
    routing {
        route("/auth") {
            get("") {
                call.respondHtml(
                    view = BaseLayout {
                        title = "Login"
                        body = {
                            FullPageContainer {
                                CenterContainer {
                                    Panel {
                                        h1 { +"Hello, world!" }
                                    }
                                }
                            }
                        }
                    })
            }
        }
    }
}