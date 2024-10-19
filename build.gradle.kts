plugins {
    kotlin("jvm").version(libs.versions.kotlin).apply(false)
    kotlin("plugin.serialization").version(libs.versions.kotlin).apply(false)
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

