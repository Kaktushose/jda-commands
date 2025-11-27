import org.jreleaser.version.SemanticVersion

allprojects {
    version = "4.0.1"
    if (System.getenv("JRELEASER_DEPLOY_MAVEN_MAVENCENTRAL_ACTIVE") == "SNAPSHOT") {
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
