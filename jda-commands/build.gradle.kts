plugins {
    `java-library`
    `maven-publish`
    jacoco
    signing
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

group = "io.github.kaktushose"
version = "4.0.0-beta.4"
description = "jda-commands"

java {
    targetCompatibility = JavaVersion.VERSION_23
    sourceCompatibility = JavaVersion.VERSION_23
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name.set("JDA-Commands")
                description.set("A declarative, annotation driven interaction framework for JDA")
                url.set("https://github.com/Kaktushose/jda-commands")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }

                developers {
                    developer {
                        name.set("Kaktushose")
                    }
                    developer {
                        name.set("Goldmensch")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/kaktushose/jda-commands.git")
                    developerConnection.set("scm:git:ssh://github.com/kaktushose/jda-commands.git")
                    url.set("https://github.com/Kaktushose/jda-commands")
                }
            }
        }
    }
}

tasks.publish {
    dependsOn("generatePomFileForMavenJavaPublication")
    dependsOn("packageForMavenCentral")
}

signing {
    useGpgCmd()
    sign(publishing.publications["mavenJava"])
}

// Task to generate checksums
tasks.register("generateChecksums") {
    group = "verification"
    description = "Generates SHA1 and MD5 checksums for artifacts"

    doLast {
        val artifacts = listOf(
            tasks.named<Jar>("javadocJar").get().archiveFile.get().asFile,
            tasks.named<Jar>("sourcesJar").get().archiveFile.get().asFile,
            tasks.named<Jar>("jar").get().archiveFile.get().asFile,
            file("${layout.buildDirectory.asFile.get().absolutePath}/publications/mavenJava/pom-default.xml")
        )

        artifacts.forEach { artifact ->
            println(artifact.absolutePath)
            val sha1File = File(artifact.absolutePath + ".sha1")
            val md5File = File(artifact.absolutePath + ".md5")

            sha1File.writeText(artifact.inputStream().use { it.readBytes().sha1Hex() })
            md5File.writeText(artifact.inputStream().use { it.readBytes().md5Hex() })
        }
    }
}

// Task to create the ZIP file
tasks.register<Zip>("packageForMavenCentral") {
    group = "build"
    val buildDir = layout.buildDirectory.asFile.get().absolutePath
    val destinationDir = "io/github/kaktushose/jda-commands/${version}"
    dependsOn("generateChecksums")
    dependsOn("signMavenJavaPublication")

    from(tasks.named("javadocJar")) {
        into(destinationDir)
    }
    from(tasks.named("sourcesJar")) {
        into(destinationDir)
    }
    from(tasks.named("jar")) {
        into(destinationDir)
    }
    from("$buildDir/publications/mavenJava") {
        include("pom-default.xml")
        into(destinationDir)
        rename("pom-default.xml", "${project.name}-${version}.pom")
    }
    from("$buildDir/publications/mavenJava") {
        include("pom-default.xml.asc")
        into(destinationDir)
        rename("pom-default.xml.asc", "${project.name}-${version}.pom.asc")
    }
    from("$buildDir/publications/mavenJava") {
        include("pom-default.xml.md5")
        into(destinationDir)
        rename("pom-default.xml.md5", "${project.name}-${version}.pom.md5")
    }
    from("$buildDir/publications/mavenJava") {
        include("pom-default.xml.sha1")
        into(destinationDir)
        rename("pom-default.xml.sha1", "${project.name}-${version}.pom.sha1")
    }
    from("$buildDir/libs") {
        include("*.asc", "*.md5", "*sha1")
        into(destinationDir)
    }
    archiveFileName.set("${project.name}-${version}-maven-central.zip")
    destinationDirectory.set(file("$buildDir/distributions"))
}

// Utility functions for checksums
fun ByteArray.sha1Hex(): String {
    return MessageDigest.getInstance("SHA-1")
        .digest(this)
        .joinToString("") { "%02x".format(it) }
}

fun ByteArray.md5Hex(): String {
    return MessageDigest.getInstance("MD5")
        .digest(this)
        .joinToString("") { "%02x".format(it) }
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

tasks.withType<Javadoc> {
    val options = options as StandardJavadocDocletOptions
    options.encoding = "UTF-8"
    options.addBooleanOption("Xdoclint:none,-missing", true)
    options.tags("apiNote:a:API Note:", "implSpec:a:Implementation Requirements:", "implNote:a:Implementation Note:")
    options.overview = "src/main/javadoc/overview.md"
    options.links = listOf(
        "https://docs.jda.wiki/"
    )


    // doesn't work anyway, f u gradle
    //    options.docFilesSubDirs(true)
    doLast {
        copy {
            include("**/doc-files/*")
            from("src/main/javadoc")
            into(project.layout.buildDirectory.dir("docs/javadoc"))
        }

    }
}