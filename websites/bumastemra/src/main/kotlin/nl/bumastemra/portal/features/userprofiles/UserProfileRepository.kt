@file:OptIn(ExperimentalUuidApi::class)

package nl.bumastemra.portal.features.userprofiles

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

enum class ProfileRole(val value: String) {
    Owner("owner"),
    Manager("manager"),
}

data class UserProfile(
    val id: Uuid,
    val userId: Uuid,
    val relationNumber: Int,
    val relationName: String,
    val role: ProfileRole,
)

interface UserProfileRepository {
    suspend fun getUserProfile(profileId: Uuid): UserProfile?
    suspend fun getUserProfiles(userId: Uuid): List<UserProfile>
}

class InMemoryUserProfileRepository(
    private val profiles: MutableList<UserProfile> = mutableListOf()
) : UserProfileRepository {

    override suspend fun getUserProfile(profileId: Uuid): UserProfile? {
        return profiles.find { it.id == profileId }
    }

    override suspend fun getUserProfiles(userId: Uuid): List<UserProfile> {
        return profiles.filter { it.userId == userId }
    }
}