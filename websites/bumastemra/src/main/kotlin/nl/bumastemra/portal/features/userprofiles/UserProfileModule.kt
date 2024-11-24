@file:OptIn(ExperimentalUuidApi::class)

package nl.bumastemra.portal.features.userprofiles

import org.kodein.di.DI
import org.kodein.di.bindProvider
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

val UserProfileModule = DI.Module("UserProfileModule") {
    bindProvider<UserProfileRepository> {
        InMemoryUserProfileRepository(
            profiles = mutableListOf(
                UserProfile(id = Uuid.parse("5cb16eee-e425-4453-aa03-241a5625beac"), userId = Uuid.parse("9a38d348-408f-4df0-9914-8960e4e53f8b"), relationNumber = 12345, relationName = "Tijs Verwest", role = ProfileRole.Owner),
                UserProfile(id = Uuid.parse("1db3caf5-f577-4cd0-8198-8a443a819464"), userId = Uuid.parse("9a38d348-408f-4df0-9914-8960e4e53f8b"), relationNumber = 54321, relationName = "Armin van Buuren", role = ProfileRole.Manager),
                UserProfile(id = Uuid.parse("462ec546-a10f-4c8e-9b2a-e26dc40b7654"), userId = Uuid.parse("9a38d348-408f-4df0-9914-8960e4e53f8b"), relationNumber = 67890, relationName = "Hardwell", role = ProfileRole.Manager),
            )
        )
    }
}