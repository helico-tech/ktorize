package nl.bumastemra.portal.libraries.layout

import assets
import kotlinx.html.*

fun PortalLayout(title: String, content: FlowContent.() -> Unit) = BaseLayout(title) {
    classes += "kt-portal-layout"
    header("container") {
        div("container") {
            img {
                src = assets.img.logo_svg
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