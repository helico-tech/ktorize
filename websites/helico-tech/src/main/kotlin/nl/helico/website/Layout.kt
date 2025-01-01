package nl.helico.website

import kotlinx.html.*
import nl.helico.ktorize.hotwire.turbo.TurboRefreshMethod
import nl.helico.ktorize.hotwire.turbo.enableViewTransition
import nl.helico.ktorize.hotwire.turbo.turboPrefetch
import nl.helico.ktorize.hotwire.turbo.turboRefreshMethod
import nl.helico.ktorize.html.HTMLView

fun Layout(
    lang: String = "nl",
    title: String = "Helico Tech",
): HTMLView = {

    this.lang = lang

    head {
        title(content = title)

        link(rel = "icon", href = "/assets/img/logo.svg")

        link(rel = "preconnect", href = "https://fonts.googleapis.com")
        link(rel = "preconnect", href = "https://fonts.gstatic.com") {
            attributes["crossorigin"] = "anonymous"
        }
        link(
            rel = "stylesheet",
            href = "https://fonts.googleapis.com/css2?family=Inter:ital,opsz,wght@0,14..32,100..900;1,14..32,100..900&display=swap"
        )

        turboPrefetch(false)
        turboRefreshMethod(TurboRefreshMethod.Morph)
        enableViewTransition()
    }

    body {
        header {

        }

        main {

        }

        footer {

        }
    }
}