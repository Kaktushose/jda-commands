plugins {
    id("io.freefair.aggregate-javadoc") version("8.12.1")
}

repositories {
    mavenCentral()
}

dependencies {
    javadoc(project(":core"))
    javadoc(project(":guice-extension"))
}

tasks.withType<Javadoc>().configureEach {
    println("rootdir: " + project.rootDir)

    val options = options as StandardJavadocDocletOptions
    options.encoding = "UTF-8"
    options.addBooleanOption("Xdoclint:none,-missing", true)
    options.tags("apiNote:a:API Note:", "implSpec:a:Implementation Requirements:", "implNote:a:Implementation Note:")
    options.overview = "src/main/javadoc/overview.md"
    options.links = listOf(
        "https://google.github.io/guice/api-docs/7.0.0/javadoc/",
        "https://kaktushose.github.io/jda-commands/javadocs/latest/",
        "https://docs.jda.wiki/"
    )

    val mspFile = project.layout.buildDirectory.dir("tmp/javadoc/modules.options").get().asFile
    outputs.file(mspFile)

    doFirst {
        mspFile.delete()
        mspFile.appendText("--module-source-path io.github.kaktushose.jda.commands.core=${project.rootDir.resolve("core/src/main/java/")}\n")
        mspFile.appendText("--module-source-path io.github.kaktushose.jda.commands.guice.extension=${project.rootDir.resolve("guice-extension/src/main/java")}")
    }

    options.optionFiles = listOf(mspFile)

    doLast {
        copy {
            include("**/doc-files/*")
            from("src/main/javadoc")
            into(project.layout.buildDirectory.dir("docs/javadoc"))
        }

    }
}



