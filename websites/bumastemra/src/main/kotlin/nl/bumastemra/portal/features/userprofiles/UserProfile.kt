package nl.bumastemra.portal.features.userprofiles

enum class ProfileRole(val value: String) {
    Owner("owner"),
    Manager("manager"),
    Administration("administration");

    companion object {
        fun fromValue(value: String): ProfileRole = ProfileRole.entries.first { it.value == value }
    }
}

data class UserProfile(
    val id: String,
    val userId: String,
    val relationNumber: Int,
    val relationName: String,
    val role: ProfileRole,
)
