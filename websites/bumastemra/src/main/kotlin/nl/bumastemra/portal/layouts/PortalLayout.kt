package nl.bumastemra.portal.layouts

import kotlinx.html.*
import nl.helico.ktorize.hotwire.turbo.turboPrefetch
import nl.helico.ktorize.html.HTMLView

fun PortalLayout(title: String = "Buma Stemra Portal", classes: Set<String> = emptySet(), topMenu: DIV.() -> Unit = {}, content: MAIN.() -> Unit): HTMLView = {
    lang = "en"

    head {
        turboPrefetch(false)
        title(content = title)
        link(rel = "icon", href = "/assets/img/favicon.ico")
        link(rel = "preconnect", href = "https://fonts.googleapis.com")
        link(rel = "preconnect", href = "https://fonts.gstatic.com") {
            attributes["crossorigin"] = "anonymous"
        }
        link(
            rel = "stylesheet",
            href = "https://fonts.googleapis.com/css2?family=Poppins:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;0,800;0,900;1,100;1,200;1,300;1,400;1,500;1,600;1,700;1,800;1,900&display=swap"
        )
    }

    body {
        this.classes = classes + setOf("kt-layout--portal")
        header {
            div("container") {
                a(href = "/") {
                    img {
                        src = "/assets/img/logo.svg"
                        alt = "Buma Stemra"
                    }
                }

                topMenu()
            }
        }

        main {
            content()
        }
    }
}