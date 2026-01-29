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
    api(libs.guice)
    api(project(":core"))

    implementation(libs.asm)
}

tasks.test {
    useJUnitPlatform()
}