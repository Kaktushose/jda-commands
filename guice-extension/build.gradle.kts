plugins {
    id("io.github.kaktushose.jda.commands.convention.java")
    id("io.github.kaktushose.jda.commands.convention.maven-central-deploy")
}

group = "io.github.kaktushose.jda-commands"
description = "An extension to JDA-Commands providing Google's Guice as a dependency injection framework"

repositories {
    mavenCentral()
}

dependencies {
    api(libs.com.google.inject.guice)
    api(project(":core"))

    implementation("org.ow2.asm:asm:9.9")
}

tasks.test {
    useJUnitPlatform()
}