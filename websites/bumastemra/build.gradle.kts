plugins {
    alias(libs.plugins.kotlin.jvm)
    application
    id("nl.helico.ktorize.assetmapper")
}

assetMapper {
    packageName.set("nl.bumastemra.portal.assets")
}

application {
    mainClass.set("nl.bumastemra.portal.AppKt")

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
    // ktor server
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)

    // logging
    implementation(libs.logback.classic)

    // ktorize
    implementation(projects.lib.importmap)
    implementation(projects.lib.html)
    implementation(projects.lib.hotwireStimulus)
    implementation(projects.lib.hotwireTurbo)

    // DI
    implementation(libs.kodein.di)
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}