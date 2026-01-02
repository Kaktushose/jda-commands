plugins {
    id("io.github.kaktushose.jda.commands.convention.java")
}

group = "io.github.kaktushose.jdac"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.palantir.javapoet:javapoet:0.9.0")
}