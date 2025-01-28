plugins {
    id("java-library")
}

group = "com.github.kaktushose.jda-commands"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    api(libs.com.google.inject.guice)
    api(project(":jda-commands"))
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

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Javadoc> {
    val options = options as StandardJavadocDocletOptions
    options.encoding = "UTF-8"
    options.addBooleanOption("Xdoclint:none,-missing", true)
    options.tags("apiNote:a:API Note:", "implSpec:a:Implementation Requirements:", "implNote:a:Implementation Note:")
    options.overview = "src/main/javadoc/overview.md"
    options.links = listOf(
        "https://google.github.io/guice/api-docs/7.0.0/javadoc/",
        "https://kaktushose.github.io/jda-commands/javadocs/latest/"
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