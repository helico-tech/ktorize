package nl.bumastemra.portal.features.dashboard

import kotlinx.html.h1
import nl.bumastemra.portal.libraries.layout.PortalLayout

val DashboardView = PortalLayout(title = "Dashboard") {
    h1 { + "Welkom bij Buma Stemra" }
}
