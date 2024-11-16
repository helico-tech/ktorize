plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    dependencies {
        implementation(libs.ktor.server.core)
        implementation(":asset-mapper-core")
    }
}