plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ktor)
    id("nl.helico.ktorize.assetmapper")
}

application {
    mainClass.set("nl.bumastemra.portal.AppKt")
}

dependencies {
    // ktor server
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.sessions)
    implementation(libs.ktor.server.auth)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // coroutines
    implementation(libs.kotlinx.coroutines.core)

    // auth
    implementation(libs.java.jwt)
    implementation(libs.jwks.rsa)

    // logging
    implementation(libs.logback.classic)

    // db
    implementation(libs.postgres)
    implementation(libs.hikari)

    // ktorize
    implementation(projects.lib.assetMapper)
    implementation(projects.lib.di)
    implementation(projects.lib.importmap)
    implementation(projects.lib.html)
    implementation(projects.lib.guards)
    implementation(projects.lib.hotwireTurbo)
    implementation(projects.lib.hotwireStimulus)
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}