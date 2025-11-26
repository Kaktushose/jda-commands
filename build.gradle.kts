allprojects {
    version = "4.0.1"
    val snapshotVersion = "4.1.0-SNAPSHOT"
    if (System.getenv("JRELEASER_DEPLOY_MAVEN_MAVENCENTRAL_ACTIVE") == "SNAPSHOT") {
        version = snapshotVersion
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
