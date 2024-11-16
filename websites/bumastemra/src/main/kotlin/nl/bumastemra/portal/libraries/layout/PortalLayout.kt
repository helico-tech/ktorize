package nl.bumastemra.portal.libraries.layout

import kotlinx.html.*

fun PortalLayout(title: String, content: DIV.() -> Unit) = BaseLayout(title) {
    classes += "kt-portal-layout"
    header {
        div("container") {
            img {
                src = "/assets/img/logo.svg"
                alt = "Buma Stemra"
            }
        }
    }

    main {
        div("container") {
            content()
        }
    }
}