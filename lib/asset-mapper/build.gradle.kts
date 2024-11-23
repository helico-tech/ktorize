plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    dependencies {
        implementation(libs.ktor.server.core)
        implementation(projects.lib.html)
        implementation(":asset-mapper-core")
    }
}