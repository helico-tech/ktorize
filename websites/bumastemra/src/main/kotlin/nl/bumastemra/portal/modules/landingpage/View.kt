package nl.bumastemra.portal.modules.landingpage

import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.h3
import kotlinx.html.p
import kotlinx.html.section
import nl.bumastemra.portal.modules.layout.PortalLayout
import nl.bumastemra.portal.libraries.auth.User
import nl.helico.ktorize.html.HTMLView

fun LandingPageView(user: User?): HTMLView = PortalLayout(title = "Dashboard") {
    classes += "kt-view--dashboard"

    h1 { + "Welkom bij Buma Stemra" }

    if (user != null) {
        h3 { + user.name }
    }

    p {
        + "In jouw persoonlijke omgeving meld je nieuw werk aan, zie je wat je muziek opbrengt en krijg je unieke inzichten in jouw muziekdata. Log in om te starten."
    }

    section("kt-component--panel panel") {
        h2 { + "Log in om verder te gaan" }
        p { + "Heb je al een BumaStemra account? Log in en ga direct naar je dashboard" }
        div("buttons") {
            a(classes = "kt-component--button", href = "/auth/login") { + "Inloggen" }
            a(classes = "kt-component--button") { + "Wachtwoord vergeten" }
        }
    }

    section("panels") {
        section("onboarding kt-component--panel panel") {
            h2 { + "Wel lid, geen account?" }
            p { + "Maak dan snel een account aan, zodat je toegang krijgt tot jouw persoonlijke omgeving. Het is binnen een paar stappen geregeld." }
            div("buttons") {
                a(classes = "kt-component--button") { +"Account aanmaken" }
            }
        }
        section("membership-registration kt-component--panel panel") {
            h2 { + "Nog geen lid?" }
            p { + "Dan kun je ook nog geen account aanmaken, geen werken aanmelden en dus ook niets verdienen aan je auteursrecht. Kijk hier of lid worden iets voor jou is." }
            div("buttons") {
                a(classes = "kt-component--button") { +"Word lid" }
            }
        }
        section("live kt-component--panel panel") {
            h2 { + "Setlijsten aanleveren als niet-lid" }
            p { + "Klik hieronder om setlijsten aan te leveren als je geen lid bent bij BumaStemra." }
            div("buttons") {
                a(classes = "kt-component--button") { +"Naar setlijsten" }
            }
        }
    }
}
