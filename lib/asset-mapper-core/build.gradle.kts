repositories {
    mavenCentral()
}

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    dependencies {
        implementation(libs.kotlinx.serialization.json)
    }
}