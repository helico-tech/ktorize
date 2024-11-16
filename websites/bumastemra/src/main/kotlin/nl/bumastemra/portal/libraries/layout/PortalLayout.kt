package nl.bumastemra.portal.libraries.layout

import kotlinx.html.*
import nl.bumastemra.portal.assets.Assets
import nl.bumastemra.portal.assets.asset

fun PortalLayout(title: String, content: DIV.() -> Unit) = BaseLayout(title) {
    classes += "kt-portal-layout"
    header {
        div("container") {
            img {
                src = asset(Assets.Img.Logo_svg)
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