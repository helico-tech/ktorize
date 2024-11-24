@file:OptIn(ExperimentalUuidApi::class)

package nl.bumastemra.portal.routes

import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import nl.bumastemra.portal.features.userprofiles.UserProfileRepository
import nl.bumastemra.portal.libraries.auth.updateUserSession
import nl.bumastemra.portal.libraries.auth.user
import nl.bumastemra.portal.libraries.auth.userSession
import nl.bumastemra.portal.views.IndexPageView
import nl.helico.ktorize.di.instance
import nl.helico.ktorize.html.respondHtml
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun Routing.index(
    userProfileRepository: UserProfileRepository = instance()
) {
    get("/") {
        call.queryParameters["profile"]?.let(Uuid::parse)?.let { profileId ->
            call.updateUserSession { copy(selectedProfileId = profileId.toString()) }
            call.respondRedirect("/")
        }

        val user = call.user

        val profiles = when (user) {
            null -> emptyList()
            else -> userProfileRepository.getUserProfiles(user.id)
        }

        val selectedProfileId = call.userSession.selectedProfileId

        val selectedProfile = selectedProfileId?.let { profiles.find { it.id.toString() == selectedProfileId } }

        call.respondHtml(view = IndexPageView(user = call.user, selectedProfile = selectedProfile, profiles = profiles))
    }
}