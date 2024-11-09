package nl.bumastemra.portal.libraries.layout

import kotlinx.html.*
import nl.helico.ktorize.html.HTMLView

fun BaseLayout(title: String = "Buma Stemra Portal", body: BODY.() -> Unit): HTMLView = {
    lang = "en"

    head {
        title(content = title)
        link(rel = "stylesheet", href = "/assets/css/styles.css")
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
        body()
    }
}