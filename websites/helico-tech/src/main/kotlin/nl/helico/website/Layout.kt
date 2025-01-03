package nl.helico.website

import kotlinx.html.*
import nl.helico.ktorize.hotwire.turbo.TurboRefreshMethod
import nl.helico.ktorize.hotwire.turbo.enableViewTransition
import nl.helico.ktorize.hotwire.turbo.turboPrefetch
import nl.helico.ktorize.hotwire.turbo.turboRefreshMethod
import nl.helico.ktorize.html.HTMLView
import java.time.Year
import java.util.Date

fun Layout(
    lang: String = "nl",
    title: String = "Helico Tech",
    main: MAIN.() -> Unit = {}
): HTMLView = {

    this.lang = lang
    this.classes = setOf("ht-layout")

    head {
        title(content = title)

        link(rel = "icon", href = "/assets/img/logo.svg")

        link(rel = "preconnect", href = "https://fonts.googleapis.com")
        link(rel = "preconnect", href = "https://fonts.gstatic.com") {
            attributes["crossorigin"] = "anonymous"
        }
        link(
            rel = "stylesheet",
            href = "https://fonts.googleapis.com/css2?family=Open+Sans:ital,wght@0,300..800;1,300..800&display=swap"
        )

        turboPrefetch(false)
        turboRefreshMethod(TurboRefreshMethod.Morph)
        enableViewTransition()
    }

    body {
        header {
            a(href = "/") {
                img(src = "/assets/img/logo.svg", alt = "Helico Tech")
            }

            nav {
                a(href = "/") {
                    +"Home"
                }

                a(href = "/about") {
                    +"About"
                }

                a(href = "/contact") {
                    +"Contact"
                }
            }
        }

        main {
            main()
        }

        footer {
            section {
                p {
                    + "KvK: 92080936"
                }

                p {
                    + "BTW: NL865877221B01"
                }

                p {
                    + "IBAN: NL65ABNA0129012882"
                }

                p {
                    span {
                        + "Email: "
                    }

                    a(href = "mailto:info@helico-tech.nl") {
                        + "info@helico-tech.nl"
                    }
                }

                p {
                    +"© ${Year.now().value} Helico Tech B.V."
                }
            }
        }
    }
}