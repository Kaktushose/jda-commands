plugins {
    `java-library`
    `maven-publish`
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


    testImplementation(libs.org.junit.jupiter.junit.jupiter.api)
    testImplementation(libs.org.junit.jupiter.junit.jupiter.engine)
}

group = "com.github.kaktushose"
version = "4.0.0-beta.3"
description = "jda-commands"


java.sourceCompatibility = JavaVersion.VERSION_23

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    val options = options as StandardJavadocDocletOptions
    options.encoding = "UTF-8"
    options.tags("apiNote:a:API Note:", "implSpec:a:Implementation Requirements:", "implNote:a:Implementation Note:")
    options.docFilesSubDirs(true)
    options.overview = rootDir.path.plus("src/main/javadoc/overview.md")
}