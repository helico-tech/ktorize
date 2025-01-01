plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ktor)
    id("nl.helico.ktorize.assetmapper")
}

application {
    mainClass.set("nl.helico.website.AppKt")
}

dependencies {
    // ktor server
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.client.content.negotiation)

    // coroutines
    implementation(libs.kotlinx.coroutines.core)

    // logging
    implementation(libs.logback.classic)

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