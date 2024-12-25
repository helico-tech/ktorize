package nl.bumastemra.portal.features.userprofiles

import io.exoquery.sql.jdbc.JdbcContext
import io.exoquery.sql.Sql
import io.exoquery.sql.jdbc.JdbcDriver
import io.exoquery.sql.runOn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


interface FetchUserProfilesUseCase {
    suspend fun forUser(userId: String): List<UserProfile>
    suspend fun byId(id: String): UserProfile?
}

class FetchUserProfilesUseCaseDatabase(
    private val databaseDriver: JdbcDriver
) : FetchUserProfilesUseCase {
    override suspend fun forUser(userId: String): List<UserProfile> {
        return query.runOn(databaseDriver)
    }

    override suspend fun byId(id: String): UserProfile? = withContext(Dispatchers.IO) {
        //val dsl = DSL.using(datasource, SQLDialect.POSTGRES)

        /*val result = dsl.select(
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
        }*/
        null
    }
}