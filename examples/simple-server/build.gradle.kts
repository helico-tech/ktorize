plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
}

application {
    mainClass.set("AppKt")
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)

    implementation(projects.lib.html)
    implementation(projects.lib.assetMapper)
    implementation(projects.lib.hotwireTurbo)
    implementation(projects.lib.hotwireStimulus)
    implementation(projects.lib.importmap)
}