# Setup

## Prerequisites

- Java 23 or later
- [JDA 5.x](https://github.com/discord-jda/JDA)
- <a href="https://jda.wiki/setup/logging/" target="_blank">SLF4J Implementation</a> _(not mandatory, but recommended)_

## Configuration
JDA-Commands is distributed through Maven Central, or alternately you can download the latest version
[here](https://github.com/Kaktushose/jda-commands/releases/latest)

=== "Maven"
    ```xml
    <dependency>
       <groupId>io.github.kaktushose</groupId>
       <artifactId>jda-commands</artifactId>
       <version>v4.0.0-beta.4</version>
    </dependency>
    ```
=== "Gradle (Kotlin DSL)"
    ```kotlin
    repositories {
       mavenCentral()
    }
    dependencies {
       implementation("io.github.kaktushose:jda-commands:v4.0.0-beta.4")
    }
    ```
=== "Gradle (Groovy DSL)"
    ```groovy
    repositories {
       mavenCentral()
    }
    dependencies {
       implementation 'io.github.kaktushose:jda-commands:v4.0.0-beta.4"'
    }
    ```