package io.github.kaktushose.jda.commands.convention

import org.gradle.api.JavaVersion
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.`java-library`

plugins {
    `java-library`
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