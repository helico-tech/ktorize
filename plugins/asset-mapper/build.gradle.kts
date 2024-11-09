repositories {
    gradlePluginPortal()
    mavenCentral()
}

plugins {
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
}

gradlePlugin {
    plugins {
        create("asset-mapper") {
            id = "nl.helico.ktorize.assetmapper"
            implementationClass = "nl.helico.ktorize.assetmapper.AssetMapperPlugin"
        }
    }
}

kotlin {
    dependencies {
        implementation(libs.ktor.utils)
    }
}