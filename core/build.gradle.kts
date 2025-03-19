plugins {
    id("io.github.kaktushose.jda.commands.convention.java")
    id("io.github.kaktushose.jda.commands.convention.maven-central-deploy")
    jacoco
}

repositories {
    mavenCentral()
}

dependencies {
    api(libs.net.dv8tion.jda)
    api(libs.org.reflections.reflections)
    api(libs.org.slf4j.slf4j.api)
    api(libs.com.fasterxml.jackson.core)
    api(libs.org.jetbrains.annotations)

    compileOnly("jakarta.inject:jakarta.inject-api:2.0.1")


    testImplementation(libs.org.junit.jupiter.junit.jupiter.api)
    testImplementation(libs.org.junit.jupiter.junit.jupiter.engine)
}

group = "io.github.kaktushose.jda-commands"
description = "The base module of jda-commands"

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



