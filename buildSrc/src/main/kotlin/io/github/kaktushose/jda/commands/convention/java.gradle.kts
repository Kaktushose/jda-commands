package io.github.kaktushose.jda.commands.convention

import org.gradle.api.JavaVersion
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.`java-library`

plugins {
    `java-library`
}

java {
    val javaVersion = property("java.version") as String

    targetCompatibility = JavaVersion.valueOf("VERSION_${javaVersion}")
    sourceCompatibility = JavaVersion.valueOf("VERSION_${javaVersion}")

    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }

    withSourcesJar()
    withJavadocJar()
}