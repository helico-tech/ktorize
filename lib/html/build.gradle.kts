plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(libs.kotlinx.html)

    implementation(libs.ktor.server.core)

    testImplementation(kotlin("test"))
}