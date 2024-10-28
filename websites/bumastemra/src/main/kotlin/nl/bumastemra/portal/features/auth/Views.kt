package nl.bumastemra.portal.features.auth

import kotlinx.html.body
import kotlinx.html.h1
import nl.helico.ktorize.html.HTMLView

val LoginPage : HTMLView = {
    body {
        h1 { +"Hello, world!" }
    }
}