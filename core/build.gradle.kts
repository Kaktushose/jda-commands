plugins {
    id("io.github.kaktushose.jda.commands.convention.java")
    id("io.github.kaktushose.jda.commands.convention.maven-central-deploy")
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    annotationProcessor(libs.propane)

    api(libs.jspecify)
    api(libs.jda)
    api(libs.bundles.slf4j)
    api(libs.jackson)
    api(libs.jetbrains.annotations)
    api(libs.proteus)
    api(libs.fluava)
    api(libs.jemoji)
    api(libs.propane)

    implementation(libs.classgraph)

    annotationProcessor(project(":processor"))

    compileOnly(project(":processor"))
    compileOnly(libs.jakarta)

    testImplementation(project(":testing"))
    mockitoAgent(libs.mockito.core) { isTransitive = false }
}

group = "io.github.kaktushose.jda-commands"
description = "The base module of jda-commands"

tasks.test {
    useJUnitPlatform()
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
}

tasks.named<JavaCompile>("compileTestJava") {
    options.compilerArgs.add("-parameters")
}