plugins {
    id("io.github.kaktushose.jda.commands.convention.java")
}

group = "io.github.kaktushose.jdac"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.javapoet)
}