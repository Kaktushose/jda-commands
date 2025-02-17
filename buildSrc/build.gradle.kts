plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jreleaser:org.jreleaser.gradle.plugin:1.16.0")
}