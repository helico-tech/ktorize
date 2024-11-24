package nl.bumastemra.portal.features.userprofiles

import org.kodein.di.DI
import org.kodein.di.bindProvider

val UserProfileModule = DI.Module("UserProfileModule") {
    bindProvider<UserProfileRepository> { InMemoryUserProfileRepository() }
}