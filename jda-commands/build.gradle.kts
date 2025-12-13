plugins {
    // don't change order!
    id("io.freefair.aggregate-javadoc") version("8.12.2.1")

    id("io.github.kaktushose.jda.commands.convention.java")
    id("io.github.kaktushose.jda.commands.convention.maven-central-deploy")
}

description = "A declarative, annotation driven interaction framework for JDA"
group = "io.github.kaktushose"

repositories {
    mavenCentral()
}

dependencies {
    javadoc(project(":core"))
    javadoc(project(":guice-extension"))
    
    api(project(":core"))
    api(project(":guice-extension"))
}

tasks.withType<Javadoc>().configureEach {
    val options = options as StandardJavadocDocletOptions
    options.overview = "src/main/javadoc/overview.md"
    options.links = listOf(
        "https://google.github.io/guice/api-docs/7.0.0/javadoc/",
        "https://docs.jda.wiki/"
    )

    doLast {
        copy {
            include("**/doc-files/*")
            from("src/main/javadoc")
            into(project.layout.buildDirectory.dir("docs/javadoc"))
        }
    }
}
