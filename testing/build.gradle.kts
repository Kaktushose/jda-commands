plugins {
    id("io.github.kaktushose.jda.commands.convention.java")
}

repositories {
    mavenCentral()
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    api(project(":core"))
    api(project(":guice-extension"))
    api(libs.junit)
    api(libs.mockito.core)
    api(libs.slf4j.simple)
    api("org.junit.platform:junit-platform-launcher")

    mockitoAgent(libs.mockito.core) { isTransitive = false }
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
}

tasks.named<JavaCompile>("compileTestJava") {
    options.compilerArgs.add("-parameters")
}