@file:OptIn(ExperimentalUuidApi::class)

package nl.bumastemra.portal.views

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.b
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.p
import kotlinx.html.section
import nl.bumastemra.portal.features.userprofiles.UserProfile
import nl.bumastemra.portal.layouts.PortalLayout
import nl.bumastemra.portal.libraries.auth.User
import nl.helico.ktorize.html.HTMLView
import kotlin.collections.plus
import kotlin.uuid.ExperimentalUuidApi

fun IndexPageView(
    user: User?,
    selectedProfile: UserProfile?,
    profiles: List<UserProfile>
): HTMLView = PortalLayout(title = "MijBumaStemra") {
    classes += "kt-view--index"

    Intro()

    if (user == null) {
        LoginPanel()
    } else {
        ProfilePickerPanel(user, selectedProfile, profiles)
    }

    Panels {
        if (user == null) OnBoardingPanel()
        MembershipRegistrationPanel()
        LivePanel()
    }
}

internal fun FlowContent.Intro() {
    h1 { + "Welkom bij Buma Stemra" }
    p {
        + "In jouw persoonlijke omgeving meld je nieuw werk aan, zie je wat je muziek opbrengt en krijg je unieke inzichten in jouw muziekdata. Log in om te starten."
    }
}

internal fun FlowContent.LoginPanel() {
    section("kt-component--panel") {
        h2 { + "Log in om verder te gaan" }
        p { + "Heb je al een BumaStemra account? Log in en ga direct naar je dashboard" }
        div("buttons") {
            a(classes = "kt-component--button", href = "/auth/login") { + "Inloggen" }
            a(classes = "kt-component--button") { + "Wachtwoord vergeten" }
        }
    }
}

internal fun FlowContent.Panels(inner: FlowContent.() -> Unit) {
    section("panels") {
        inner()
    }
}

internal fun FlowContent.OnBoardingPanel() {
    section("onboarding kt-component--panel") {
        h2 { + "Wel lid, geen account?" }
        p { + "Maak dan snel een account aan, zodat je toegang krijgt tot jouw persoonlijke omgeving. Het is binnen een paar stappen geregeld." }
        div("buttons") {
            a(classes = "kt-component--button") { +"Account aanmaken" }
        }
    }
}

internal fun FlowContent.MembershipRegistrationPanel() {
    section("membership-registration kt-component--panel") {
        h2 { + "Nog geen lid?" }
        p { + "Dan kun je ook nog geen account aanmaken, geen werken aanmelden en dus ook niets verdienen aan je auteursrecht. Kijk hier of lid worden iets voor jou is." }
        div("buttons") {
            a(classes = "kt-component--button") { +"Word lid" }
        }
    }
}

internal fun FlowContent.LivePanel() {
    section("live kt-component--panel panel") {
        h2 { + "Setlijsten aanleveren als niet-lid" }
        p { + "Klik hieronder om setlijsten aan te leveren als je geen lid bent bij BumaStemra." }
        div("buttons") {
            a(classes = "kt-component--button") { +"Naar setlijsten" }
        }
    }
}

internal fun FlowContent.ProfilePickerPanel(user: User, selectedProfile: UserProfile?, profiles: List<UserProfile>) {
    section("profile-picker kt-component--panel panel") {
        div("user") {
            p {
                text("Je bent ingelogd als ")
                b { +user.name }
            }

            if (selectedProfile != null) {
                p {
                    text(" op profiel ")
                    b { +selectedProfile.relationName }
                }
            }

            div("buttons") {
                a(classes = "kt-component--button", href = "/auth/logout") { +"Uitloggen" }
            }
        }

        div("profiles") {
            b { + "Kies op welk profiel je in wilt loggen:" }
            ProfileList(profiles)
        }
    }
}

internal fun FlowContent.ProfileList(profiles: List<UserProfile>) {
    div("profile-list") {
        profiles.forEach { Profile(it) }
    }
}

internal fun FlowContent.Profile(profile: UserProfile) {
    a(classes = "profile", href = "/?profile=${profile.id}") {
        div("name") { + profile.relationName }
        div("relation-number") { + profile.relationNumber.toString() }
    }
}