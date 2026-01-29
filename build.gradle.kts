import com.diffplug.spotless.LineEnding
import org.jreleaser.version.SemanticVersion

plugins {
    alias(libs.plugins.spotless)
}

repositories {
    mavenCentral()
}

allprojects {
    version = "5.0.0-SNAPSHOT"
    if (System.getenv("DEPLOY_ACTIVE") == "SNAPSHOT") {
        if (!version.toString().endsWith("-SNAPSHOT")) {
            val semver = SemanticVersion.of(version.toString())
            val snapshotSemver = SemanticVersion.of(semver.major, semver.minor + 1, "-", "SNAPSHOT", null)
            version = snapshotSemver.toString()
        }
    }
}

subprojects {
    tasks.withType<Javadoc> {
        val options = options as StandardJavadocDocletOptions

        options.encoding = "UTF-8"
        options.addBooleanOption("Xdoclint:none,-missing", true)
        options.tags("apiNote:a:API Note:", "implSpec:a:Implementation Requirements:", "implNote:a:Implementation Note:")
    }
}

spotless {
    encoding("UTF-8")

    format("misc") {
        target("*.gradle.kts", ".gitattributes", ".gitignore")

		trimTrailingWhitespace()
        endWithNewline()
    }

    java {
        target("**/*.java")
		targetExclude(".github/workflows/**")

        importOrder("io.github.kaktushose|dev.goldmensch|net.dv8tion|", "java|javax", "\\#")
        forbidModuleImports()
		trimTrailingWhitespace()
        endWithNewline()
    }

    format("markdown") {
        target("*.md")

        prettier()

		trimTrailingWhitespace()
        endWithNewline()
    }

}

// separate task for potential additional formatting tasks in the future
tasks.register("format") {
    group = "verification"
    dependsOn(tasks.named("spotlessApply"))
}

tasks.named("check").configure {
    dependsOn(tasks.named("spotlessCheck"))
}
