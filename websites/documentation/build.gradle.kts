plugins {
    kotlin("jvm")
    application
}

val module = "nl.helico.ktorize.web.docs.ServerKt"

application {
    mainClass.set(module)
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

tasks.register("runDev") {
    group = "application"

    val originalTask = tasks.getByName("run") as JavaExec

    originalTask.args("-config=application-dev.conf")

    dependsOn(originalTask)
}