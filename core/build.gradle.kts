plugins {
    id("io.github.kaktushose.jda.commands.convention.java")
    id("io.github.kaktushose.jda.commands.convention.maven-central-deploy")
}

repositories {
    mavenCentral()
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    api(libs.net.dv8tion.jda)
    api(libs.org.reflections.reflections)
    api(libs.org.slf4j.slf4j.api)
    api(libs.com.google.code.gson.gson)
    api(libs.org.jetbrains.annotations)

    compileOnly("jakarta.inject:jakarta.inject-api:2.0.1")


    testImplementation(libs.org.junit.jupiter)
    testImplementation(libs.org.mockito.core)
    testImplementation(libs.org.mockito.junit)
    testImplementation(libs.org.slf4j.slf4j.simple)
    mockitoAgent(libs.org.mockito.core) { isTransitive = false }
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

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