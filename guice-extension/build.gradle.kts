plugins {
    id("java-library")
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
}

java {
    targetCompatibility = JavaVersion.VERSION_23
    sourceCompatibility = JavaVersion.VERSION_23
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
    withSourcesJar()
    withJavadocJar()
}

tasks.test {
    useJUnitPlatform()
}