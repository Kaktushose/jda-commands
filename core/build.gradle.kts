plugins {
    id("io.github.kaktushose.jda.commands.convention.java")
    id("io.github.kaktushose.jda.commands.convention.maven-central-deploy")
}

repositories {
    mavenCentral()
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    api(libs.org.jspecify)
    api(libs.net.dv8tion.jda)
    api(libs.org.reflections.reflections)
    api(libs.org.slf4j.slf4j.api)
    api(libs.com.fasterxml.jackson.core)
    api(libs.org.jetbrains.annotations)
    api(libs.io.github.kaktushose.proteus)
    api(libs.dev.goldmensch.fluava)

    compileOnly("jakarta.inject:jakarta.inject-api:2.0.1")

    testImplementation(project(":testing"))
    mockitoAgent(libs.org.mockito.core) { isTransitive = false }
}

group = "io.github.kaktushose.jda-commands"
description = "The base module of jda-commands"

tasks.test {
    useJUnitPlatform()
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
}

tasks.named<JavaCompile>("compileTestJava") {
    options.compilerArgs.add("-parameters")
}