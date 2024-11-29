allprojects {
    group = "nl.helico.ktorize"
    version = "0.0.1-SNAPSHOT"

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    alias(libs.plugins.kotlin.jvm).apply(false)
    alias(libs.plugins.ktor).apply(false)
    alias(libs.plugins.kotlinx.serialization).apply(false)
    alias(libs.plugins.jooq).apply(false)
    id("nl.helico.ktorize.assetmapper").apply(false)
}

