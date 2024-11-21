plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    application
    id("nl.helico.ktorize.assetmapper")
}

application {
    mainClass.set("nl.bumastemra.portal.AppKt")

    val environment = System.getenv("APP_ENV")

    if (environment != null) {
        logger.lifecycle("Using application.$environment.conf")
        applicationDefaultJvmArgs = listOf(
            "-Dconfig.resource=application.$environment.conf",
            "-Dlogback.configurationFile=logback.$environment.xml"
        )
    }
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

    // auth
    implementation(libs.java.jwt)
    implementation(libs.jwks.rsa)

    // logging
    implementation(libs.logback.classic)

    // ktorize
    implementation(projects.lib.assetMapper)
    implementation(projects.lib.importmap)
    implementation(projects.lib.html)
    implementation(projects.lib.hotwireStimulus)
    implementation(projects.lib.hotwireTurbo)

    // DI
    implementation(libs.kodein.di)
    implementation("io.ktor:ktor-server-core-jvm:3.0.0")
    implementation("io.ktor:ktor-server-sessions-jvm:3.0.0")
    implementation("io.ktor:ktor-server-core-jvm:3.0.0")
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}