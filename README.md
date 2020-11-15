[![Generic badge](https://img.shields.io/badge/Download-1.0.1-green.svg)](https://github.com/Kaktushose/jda-commands/releases/tag/v.1.0.1)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/f2b4367f6d0f42d89b7e51331f3ce299)](https://www.codacy.com/manual/Kaktushose/jda-commands?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Kaktushose/jda-commands&amp;utm_campaign=Badge_Grade)
![license-shield](https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg)

# JDA-Commands

A simple yet highly customizable, annotation driven command framework for [JDA](https://github.com/DV8FromTheWorld/JDA). 

- Current Version: [1.0.1](https://github.com/Kaktushose/jda-commands/releases/tag/v.1.0.1)
- JDA Version: [4.2.0_191](https://bintray.com/dv8fromtheworld/maven/JDA/4.2.0_191)
- [JavaDoc](https://kaktushose.github.io/jda-commands/index.html)
- [Wiki](https://github.com/Kaktushose/jda-commands/wiki)


## Features

The focus of this framework is strongly oriented towards the reduction of boilerplate code. This goes along with the aim to reduce configuration steps that are needed before startup. Therefore IoC, Dependency Injection and Declarative Programming are the key concepts of this framework. Nevertheless, there is a high level of customization, if wanted, every part of this framework can be replaced by an own implementation.  

The core features of this framework are as following:

- Fully annotation driven command declaration 
- Argument Parsing based on method signature
- Label shortening (comparable to auto complete)
- Basic implementation of Dependency Injection
- Automatic error resolving  
- Automatically generated Help Commands & Error Messages
- Command modification at runtime
- Many settings such as Guild specific prefixes

**Example**

The following example will demonstrate how easy it is to write a command:

```java
@CommandController
public class GreetCommand {

    @Command("greet")
    public void greet(CommandEvent event, Member member) {
        event.reply("Hello %s!", member.getAsMention());
    }

}
```

If you want to learn more, check out the [Wiki](https://github.com/Kaktushose/jda-commands/wiki).

## Download

You can download the latest version [here](https://github.com/Kaktushose/jda-commands/releases/tag/v.1.0.1).

## Maven

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
```xml
<dependency>
    <groupId>com.github.Kaktushose</groupId>
    <artifactId>jda-commands</artifactId>
    <version>VERSION</version>
</dependency>
```

## Gradle
```groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
```groovy
dependencies {
	        implementation 'com.github.Kaktushose:jda-commands:VERSION'
	}
```

## Contributing

If you think that something is missing and you want to add it yourself, feel free to open a pull request. Please try to keep your code quality as good as mine and stick to the core concepts of this framework.

## Dependencies

The following dependencies were used to build this framework:

* JDA
    * Version: 4.2.0_191
    * [Github](https://github.com/DV8FromTheWorld/JDA)
* Reflections
    * Version: 0.9.10
    * [Github](https://github.com/ronmamo/reflections)
* Jackson
    * Version: 2.11.0
    * [Github](https://github.com/FasterXML/jackson)
* slf4j-api
    * Version: 1.7.30
    * [Website](http://www.slf4j.org/)

 
