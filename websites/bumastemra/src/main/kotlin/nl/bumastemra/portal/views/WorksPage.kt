package nl.bumastemra.portal.views

import kotlinx.html.*
import nl.bumastemra.portal.layouts.LoggedInLayout
import nl.helico.ktorize.html.HTMLView

fun WorksPage(): HTMLView = LoggedInLayout {
    div {
        h1 {
            +"Works"
        }
    }
}