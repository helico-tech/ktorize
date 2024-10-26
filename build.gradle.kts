plugins {
    alias(libs.plugins.kotlin.jvm).version(libs.versions.kotlin).apply(false)
    alias(libs.plugins.kotlinx.serialization).version(libs.versions.kotlin).apply(false)
}

allprojects {
    group = "nl.helico.ktorize"
    version = "0.0.1-SNAPSHOT"

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

