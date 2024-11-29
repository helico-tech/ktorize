package nl.bumastemra.portal.features.userprofiles

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nl.bumastemra.portal.db.platform_api.tables.Relations.Companion.RELATIONS
import nl.bumastemra.portal.db.platform_api.tables.UserProfiles
import nl.bumastemra.portal.db.platform_api.tables.UserProfiles.Companion.USER_PROFILES
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.util.UUID
import javax.sql.DataSource

interface FetchUserProfilesUseCase {
    suspend fun forUser(userId: String): List<UserProfile>
    suspend fun byId(id: String): UserProfile?
}

class FetchUserProfilesUseCaseDatabase(
    private val datasource: DataSource
) : FetchUserProfilesUseCase {
    override suspend fun forUser(userId: String): List<UserProfile> = withContext(Dispatchers.IO) {
        val dsl = DSL.using(datasource, SQLDialect.POSTGRES)

        val result = dsl.select(
            USER_PROFILES.IDENTIFIER,
            USER_PROFILES.USER_ID,
            USER_PROFILES.BUSINESS_RELATION_NUMBER,
            RELATIONS.NAME,
            USER_PROFILES.ROLE
        )
        .from(USER_PROFILES)
        .join(RELATIONS).on(USER_PROFILES.BUSINESS_RELATION_NUMBER.eq(RELATIONS.RELATION_NUMBER))
        .where(USER_PROFILES.USER_ID.eq(UUID.fromString(userId)))
        .orderBy(RELATIONS.NAME.asc())

        result.fetch().map { record ->
            UserProfile(
                id = record[USER_PROFILES.IDENTIFIER].toString(),
                userId = record[USER_PROFILES.USER_ID].toString(),
                relationNumber = record[USER_PROFILES.BUSINESS_RELATION_NUMBER]!!,
                relationName = record[RELATIONS.NAME]!!,
                role = ProfileRole.fromValue(record[USER_PROFILES.ROLE]!!)
            )
        }
    }

    override suspend fun byId(id: String): UserProfile? = withContext(Dispatchers.IO) {
        val dsl = DSL.using(datasource, SQLDialect.POSTGRES)

        val result = dsl.select(
            USER_PROFILES.IDENTIFIER,
            USER_PROFILES.USER_ID,
            USER_PROFILES.BUSINESS_RELATION_NUMBER,
            RELATIONS.NAME,
            USER_PROFILES.ROLE
        )
        .from(USER_PROFILES)
        .join(RELATIONS).on(USER_PROFILES.BUSINESS_RELATION_NUMBER.eq(RELATIONS.RELATION_NUMBER))
        .where(USER_PROFILES.IDENTIFIER.eq(UUID.fromString(id)))

        result.fetchOne()?.let { record ->
            UserProfile(
                id = record[USER_PROFILES.IDENTIFIER].toString(),
                userId = record[USER_PROFILES.USER_ID].toString(),
                relationNumber = record[USER_PROFILES.BUSINESS_RELATION_NUMBER]!!,
                relationName = record[RELATIONS.NAME]!!,
                role = ProfileRole.fromValue(record[USER_PROFILES.ROLE]!!)
            )
        }
    }
}