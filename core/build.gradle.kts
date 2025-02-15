import org.jreleaser.model.Active
import org.jreleaser.model.api.signing.Signing
import software.amazon.awssdk.core.internal.signer.SigningMethod

plugins {
    `java-library`
    jacoco
    id("io.github.kaktushose.jda.commands.convention.maven-central-deploy")
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    api(libs.net.dv8tion.jda)
    api(libs.org.reflections.reflections)
    api(libs.org.slf4j.slf4j.api)
    api(libs.com.google.code.gson.gson)
    api(libs.org.jetbrains.annotations)

    compileOnly("jakarta.inject:jakarta.inject-api:2.0.1")


    testImplementation(libs.org.junit.jupiter.junit.jupiter.api)
    testImplementation(libs.org.junit.jupiter.junit.jupiter.engine)
}

group = "io.github.kaktushose.jda-commands"
description = "The base module of jda-commands"
version = "0.0.1"

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

    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}


tasks.jacocoTestReport {
    reports {
        xml.required = true
    }
    dependsOn(tasks.test) // tests are required to run before generating the report
}



