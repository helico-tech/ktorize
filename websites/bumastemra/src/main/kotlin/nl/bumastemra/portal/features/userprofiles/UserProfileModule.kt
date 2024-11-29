@file:OptIn(ExperimentalUuidApi::class)

package nl.bumastemra.portal.features.userprofiles

import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance
import kotlin.uuid.ExperimentalUuidApi

val UserProfileModule = DI.Module("UserProfileModule") {
    bindProvider<FetchUserProfilesUseCase> { FetchUserProfilesUseCaseDatabase(instance()) }
}