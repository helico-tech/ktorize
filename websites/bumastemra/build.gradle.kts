plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.jooq)

    id("nl.helico.ktorize.assetmapper")

    application
}

application {
    mainClass.set("nl.bumastemra.portal.AppKt")
}

dependencies {
    // ktor server
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactive)

    // auth
    implementation(libs.java.jwt)
    implementation(libs.jwks.rsa)

    // logging
    implementation(libs.logback.classic)

    // db
    implementation(libs.jooq)
    implementation(libs.postgres)
    implementation(libs.hikari)
    jooqGenerator(libs.postgres)

    // ktorize
    implementation(projects.lib.assetMapper)
    implementation(projects.lib.di)
    implementation(projects.lib.importmap)
    implementation(projects.lib.html)
    implementation(projects.lib.guards)
    implementation(projects.lib.hotwireStimulus)
    implementation(projects.lib.hotwireTurbo)

    implementation("io.ktor:ktor-server-core-jvm:3.0.0")
    implementation("io.ktor:ktor-server-sessions-jvm:3.0.0")
    implementation("io.ktor:ktor-server-core-jvm:3.0.0")
}

kotlin {
    jvmToolchain(21)
}

jooq {
    version.set(libs.versions.jooq)

    configurations {
        create("main") {
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:postgresql://localhost:5432/platform_api"
                    user = "postgres"
                    password = "postgres"
                }

                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        includes = "user_profiles|relations"
                    }
                    target.apply {
                        packageName = "nl.bumastemra.portal.db.platform_api"
                        directory = "src/main/generated/jooq/platform_api"
                    }
                }
            }
        }
    }
}

sourceSets {
    main {
        kotlin {
            srcDir("src/main/generated")
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}