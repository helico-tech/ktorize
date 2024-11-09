plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    id("nl.helico.ktorize.assetmapper")
}

assetMapper {
    //assetDirectory.set(file("src/main/resources/assets"))
}

application {
    mainClass.set("AppKt")

    val environment = System.getenv("APP_ENV")

    if (environment != null) {
        logger.lifecycle("Using application.$environment.conf")
        applicationDefaultJvmArgs = listOf(
            "-Dconfig.resource=application.$environment.conf",
            "-Dlogback.configurationFile=logback.$environment.xml"
        )
    }
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)

    implementation(projects.lib.html)
    implementation(projects.lib.di)
    //implementation(projects.lib.assetMapper)
    implementation(projects.lib.hotwireTurbo)
    implementation(projects.lib.hotwireStimulus)
    implementation(projects.lib.importmap)
}