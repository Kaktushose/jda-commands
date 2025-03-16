package io.github.kaktushose.jda.commands.convention

import org.jreleaser.model.Active

plugins {
    `maven-publish`
    id("org.jreleaser")
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

    repositories {
        maven {
            setUrl(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

jreleaser {
    project {
        copyright = "Kaktushose & Goldmensch"
    }


    signing {
        active = Active.ALWAYS
        armored = true
    }

    deploy {
        maven {
            mavenCentral {
                create("sonatype") {
                    active = Active.ALWAYS
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository("build/staging-deploy")
                    setStage("UPLOAD")
                }
            }
        }
    }
}

tasks.jreleaserDeploy {
    dependsOn(tasks.publish)
}