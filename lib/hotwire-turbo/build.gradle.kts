plugins {
    kotlin("jvm")
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(projects.lib.html)
}