import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.serialization") version "1.7.21"
    application
}

group = "moe.lina"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.proxyfox.dev")
}

dependencies {
    implementation("dev.kord:kord-core:0.8.0-M17")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

    implementation("io.ktor:ktor-client-core:2.2.3")
    implementation("io.ktor:ktor-client-cio:2.2.3")
    implementation("io.ktor:ktor-client-content-negotiation:2.2.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.2.3")

    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("io.ktor:ktor-client-logging-jvm:2.2.3")

    implementation("io.arrow-kt:arrow-core:1.1.2")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("moe.lina.hafsa.MainKt")
}