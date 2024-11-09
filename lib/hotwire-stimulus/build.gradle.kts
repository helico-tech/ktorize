plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.ktor.server.core)

    implementation(projects.lib.html)
    implementation(projects.lib.importmap)

    testImplementation(kotlin("test"))
    testImplementation(libs.ktor.server.test.host)
}