package nl.bumastemra.portal.features.dashboard

import kotlinx.html.classes
import kotlinx.html.h1
import kotlinx.html.p
import nl.bumastemra.portal.libraries.layout.PortalLayout

val DashboardView = PortalLayout(title = "Dashboard") {
    classes += "kt-dashboard-view"

    h1 { + "Welkom bij Buma Stemra" }
    p {
        + "In jouw persoonlijke omgeving meld je nieuw werk aan, zie je wat je muziek opbrengt en krijg je unieke inzichten in jouw muziekdata. Log in om te starten."
    }
}
