allprojects {
    version = "4.0.0-beta.8"
    if (System.getenv("JRELEASER_DEPLOY_MAVEN_MAVENCENTRAL_ACTIVE") == "SNAPSHOT") {
        version = "${version.toString().split("-")[0]}-SNAPSHOT"
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