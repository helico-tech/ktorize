plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.kotlinx.html)
    implementation(libs.kotlinx.serialization.json)

    implementation(projects.ktorize.lib.html)

    testImplementation(kotlin("test"))
}