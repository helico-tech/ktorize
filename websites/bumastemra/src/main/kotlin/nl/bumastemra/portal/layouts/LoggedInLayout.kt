package nl.bumastemra.portal.layouts

import kotlinx.html.MAIN
import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.nav
import nl.helico.ktorize.html.HTMLView

fun LoggedInLayout(
    title: String = "Buma Stemra Portal",
    content: MAIN.() -> Unit
): HTMLView = PortalLayout(
    title = title,
    content = content,
    classes = setOf("kt-layout--logged-in"),
    topMenu = {
        nav {
            a { + "Berichten"}
            a { + "Kalender"}
            a { + "Contracten"}
            a { + "Werken" }
            a { + "Afrekeningen"}
            a { + "Inzichten" }
            a { + "Live" }
            a { + "Claims" }
        }
    }
)