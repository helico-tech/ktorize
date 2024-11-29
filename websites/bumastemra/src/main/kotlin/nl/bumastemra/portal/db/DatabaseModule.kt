package nl.bumastemra.portal.db

import com.zaxxer.hikari.HikariDataSource
import nl.helico.ktorize.di.applicationConfig
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import javax.sql.DataSource

val DatabaseModule = DI.Module("database") {
    bindSingleton<DataSource> {
        val config = HikariDataSource().apply {
            jdbcUrl = applicationConfig.property("database.platform_api.url").getString()
            username = applicationConfig.property("database.platform_api.username").getString()
            password = applicationConfig.property("database.platform_api.password").getString()
            maximumPoolSize = 10
            minimumIdle = 2
            connectionTimeout = 60000
            idleTimeout = 600000
            maxLifetime = 1800000
        }

        HikariDataSource(config)
    }
}