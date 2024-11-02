plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.ktor.server.core)

    testImplementation(kotlin("test"))
    testImplementation(libs.ktor.server.test.host)
}