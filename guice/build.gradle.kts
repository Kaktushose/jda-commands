plugins {
    id("java-library")
}

group = "com.github.kaktushose.jda-commands"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    api(libs.com.google.inject.guice)
    api(project(":jda-commands"))
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