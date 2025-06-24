plugins {
    id("io.github.kaktushose.jda.commands.convention.java")
    id("io.github.kaktushose.jda.commands.convention.maven-central-deploy")
}

repositories {
    mavenCentral()
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    api(project(":core"))
    api(project(":guice-extension"))
    api(libs.org.junit.jupiter)
    api(libs.org.mockito.core)
    api(libs.org.mockito.junit)
    api(libs.org.slf4j.slf4j.simple)
    api("org.junit.platform:junit-platform-launcher")

    mockitoAgent(libs.org.mockito.core) { isTransitive = false }
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
}

tasks.named<JavaCompile>("compileTestJava") {
    options.compilerArgs.add("-parameters")
}