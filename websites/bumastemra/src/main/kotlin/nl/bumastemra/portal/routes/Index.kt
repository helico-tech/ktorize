@file:OptIn(ExperimentalUuidApi::class)

package nl.bumastemra.portal.routes

import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import nl.bumastemra.portal.features.userprofiles.FetchUserProfilesUseCase
import nl.bumastemra.portal.libraries.auth.Profile
import nl.bumastemra.portal.libraries.auth.updateUserSession
import nl.bumastemra.portal.libraries.auth.user
import nl.bumastemra.portal.libraries.auth.userSession
import nl.bumastemra.portal.views.IndexPageView
import nl.helico.ktorize.di.instance
import nl.helico.ktorize.html.respondHtml
import kotlin.uuid.ExperimentalUuidApi

fun Routing.index(
    fetchUserProfilesUseCase: FetchUserProfilesUseCase = instance(),
) {
    get("/") {
        call.queryParameters["profile"]?.let { profileId ->

            val profile = fetchUserProfilesUseCase.byId(profileId)

            if (profile == null) {
                call.respondRedirect("/")
                return@get
            }

            call.updateUserSession { copy(profile = Profile(id = profile.id.toString(), role = profile.role.toString())) }

            call.respondRedirect("/")
            return@get
        }

        val user = call.user

        val profiles = when (user) {
            null -> emptyList()
            else -> fetchUserProfilesUseCase.forUser(user.id)
        }

        val selectedProfileId = call.userSession.profile?.id

        val selectedProfile = selectedProfileId?.let { profiles.find { it.id.toString() == selectedProfileId } }

        call.respondHtml(view = IndexPageView(user = call.user, selectedProfile = selectedProfile, profiles = profiles))
    }
}