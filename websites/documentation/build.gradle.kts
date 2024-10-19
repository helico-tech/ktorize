plugins {
    kotlin("jvm")
    application
}

application {
    mainClass.set("nl.helico.ktorize.web.docs.ServerKt")
}

dependencies {
    // ktor server
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)

    // logging
    implementation(libs.logback.classic)

    // ktorize
    implementation(projects.lib.html)
    implementation(projects.lib.hotwireTurbo)
    implementation(projects.lib.assetMapper)
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}