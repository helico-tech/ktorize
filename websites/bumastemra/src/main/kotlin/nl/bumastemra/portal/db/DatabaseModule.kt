package nl.bumastemra.portal.db

import com.zaxxer.hikari.HikariDataSource
import io.exoquery.sql.jdbc.JdbcContext
import io.exoquery.sql.jdbc.JdbcDriver
import io.exoquery.sql.jdbc.TerpalContext
import io.exoquery.sql.jdbc.TerpalDriver
import io.ktor.server.config.ApplicationConfig
import nl.helico.ktorize.di.applicationConfig
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import javax.sql.DataSource

enum class DatabaseEnum(val configName: String) {
    PlatformApi("platform_api")
}

val DatabaseModule = DI.Module("database") {

    fun getDataSource(applicationConfig: ApplicationConfig, database: DatabaseEnum): DataSource {
        return HikariDataSource().apply {
            jdbcUrl = applicationConfig.property("database.${database.configName}.url").getString()
            username = applicationConfig.property("database.${database.configName}.username").getString()
            password = applicationConfig.property("database.${database.configName}.password").getString()
            maximumPoolSize = 10
            minimumIdle = 2
            connectionTimeout = 60000
            idleTimeout = 600000
            maxLifetime = 1800000
        }
    }

    fun getJDBCContext(dataSource: DataSource): JdbcDriver = TerpalDriver.Postgres(dataSource)

    bindSingleton<DataSource>(tag = DatabaseEnum.PlatformApi) {
        getDataSource(applicationConfig, DatabaseEnum.PlatformApi)
    }

    bindSingleton<JdbcDriver>(tag = DatabaseEnum.PlatformApi) {
        getJDBCContext(dataSource = instance(tag = DatabaseEnum.PlatformApi))
    }
}