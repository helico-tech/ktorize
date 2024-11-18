package nl.bumastemra.portal.features.dashboard

import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.p
import kotlinx.html.section
import nl.bumastemra.portal.libraries.layout.PortalLayout

val DashboardView = PortalLayout(title = "Dashboard") {
    classes += "kt-view--dashboard"

    h1 { + "Welkom bij Buma Stemra" }

    p {
        + "In jouw persoonlijke omgeving meld je nieuw werk aan, zie je wat je muziek opbrengt en krijg je unieke inzichten in jouw muziekdata. Log in om te starten."
    }

    section("login") {
        h2 { + "Log in om verder te gaan" }
        p { + "Heb je al een BumaStemra account? Log in en ga direct naar je dashboard" }
        div("buttons") {
            button(classes = "kt-component--button") { + "Inloggen" }
            button(classes = "kt-component--button") { + "Wachtwoord vergeten" }
        }
    }
}
