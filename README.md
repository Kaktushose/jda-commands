[![JDA-Version](https://img.shields.io/badge/JDA%20Version-4.3.0__310-important)](https://github.com/DV8FromTheWorld/JDA#download)
[![Generic badge](https://img.shields.io/badge/Download-1.1.1-green.svg)](https://github.com/Kaktushose/jda-commands/releases/latest)
[![Java CI](https://github.com/Kaktushose/jda-commands/actions/workflows/ci.yml/badge.svg?branch=dev)](https://github.com/Kaktushose/jda-commands/actions/workflows/ci.yml)
[![Codacy Badge](https://app.codacy.com/project/badge/Coverage/f2b4367f6d0f42d89b7e51331f3ce299)](https://www.codacy.com/gh/Kaktushose/jda-commands/dashboard?utm_source=github.com&utm_medium=referral&utm_content=Kaktushose/jda-commands&utm_campaign=Badge_Coverage)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/f2b4367f6d0f42d89b7e51331f3ce299)](https://www.codacy.com/manual/Kaktushose/jda-commands?utm_source=github.com&utm_medium=referral&utm_content=Kaktushose/jda-commands&utm_campaign=Badge_Grade)
[![license-shield](https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg)]()

# JDA-Commands

An extendable, declarative and annotation driven command framework for [JDA](https://github.com/DV8FromTheWorld/JDA).

The following example will demonstrate how easy it is to write a command:

```java

import com.github.kaktushose.jda.commands.annotations.constraints.NotRole;

@CommandController
@Permission("BAN_MEMBERS")
public class BanCommand {

  @Command("ban")
  public void ban(CommandEvent event, 
                  @NotRole("admin") Member member, 
                  @Max(7) int delDays, 
                  @Optional @Concat String reason) {
    event.getGuild().ban(member, delDays);
    event.reply("%s got banned for reason %s", member.getAsMention(), reason);
  }
}
```

Finally, start the framework by calling:
```java
JDACommands.start();
```

## Features

As shown in the example, JDA-Commands makes it possible to focus only on the business logic inside your command classes.
All other chores like permission checks, argument parsing and validation, cooldowns, etc. are dealt with on the site of 
the framework.

Utility classes and methods for help and error messages, documentation, internationalization, embed generation, etc. 
further improve the workflow when writing bots.

You can find a detailed list of all features down below:

### Parameters

<details>
<summary>Type Adapting</summary>
As seen in the example, the method signature will be translated into a command syntax. When a command gets called, this
framework will adapt the raw String input to the types specified in the method signature. As a result all the
boilerplate code for parsing parameters becomes obsolete.
</details>

<details>
<summary>Parameter Validation</summary>
Parameters can have additional constraints, such as min or max value, etc. When a constraint fails, an error message
will be sent automatically. You can also define your own constraints.
</details>

### Routing

<details>
<summary>Levenshtein Distance</summary>
The Levenshtein distance between two words is the minimum number of single-character edits (insertions, deletions or
substitutions) required to change one word into the other. For instance, the input `tpyo` will match the command
label `typo`.
</details>

<details>
<summary>Label Shortening</summary>
Label shortening can be compared to the auto complete feature of a terminal. For instance, the command label `foo` will
also match the input
`f` or `fo` as long as only one command that starts with `f` (or respectively `fo`) exists. This also works for sub
command labels.
</details>

<details>
<summary>Quote Parsing</summary>
Normally arguments are split at every empty space. This makes it impossible to pass one argument that contains several
words. In order to fix this issue, the default event parser can parse quotes. In other words: The
input `label "arg0 arg1" arg2` will be parsed to `[label, arg0 arg1, arg2]` instead of `[label, "arg0, arg1", arg2]`.
</details>

<details>
<summary>Private Channel Support</summary>
If enabled, commands can also be called by sending a private message to the Bot.
</details>

### Constraints

<details>
<summary>Permissions System</summary>
The permission system supports both using discord permissions and custom permissions. By default, you can use all
permissions defined inside
JDAs [Permission Embed](https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/Permission.html). By adding your own
permission validator, you can use custom permission strings and bind permissions to certain roles or members.
</details>

<details>
<summary>Filter Chain</summary>
You can define filters that will run before each command execution. This can be useful to perform additional checks,
which aren't supported by this framework.
</details>

<details>
<summary>Cooldown System</summary>
Commands can have a per-user cooldown to rate limit the execution of commands.
</details>

### Misc

<details>
<summary>Guild Settings</summary>
Settings, such as the prefix or muted channels, are available on a per-guild level. By default, all settings apply
globally.
</details>

<details>
<summary>Help & Error Messages</summary>
The `@Command` annotation has additional attributes to document commands. These attributes are used to automatically
create Help Embeds. Furthermore, there are default Error Embeds for all validation systems of this framework. (Parameter
Constraints, Permissions, etc.)
</details>

<details>
<summary>Documentation</summary>
It's possible to generate command documentation in markdown and html format. A GitHub Action for this is also planned.
</details>

<details>
<summary>Internationalization</summary>
This framework and all the output it generates are in English. However, you can easily change the language. All embeds
sent can also be loaded from a json file, which uses placeholders.
</details>

<details>
<summary>Embed Deserialization</summary>
You can serialize and deserialize JDAs EmbedBuilder object to json. This comes in pretty handy, because for example you
don't have to recompile the whole project if you find one typo inside your embed.
</details>

<details>
<summary>Dependency Injection</summary>
This framework has a basic implementation of dependency injection, since you don't construct your command classes on
your own.
</details>

<details>
<summary>Persistence</summary>
This framework has builtin classes to store settings and user permissions in different formats, such as json or mysql.
</details>

<details>
<summary>Reflect API</summary>
Just like Javas Reflect API this framework also supports accessing and modifying command definitions at runtime.
</details>

If you want to learn more, check out the [Wiki](https://github.com/Kaktushose/jda-commands/wiki) or
the [Javadoc](https://kaktushose.github.io/jda-commands/).

## Download

You can download the latest version [here](https://github.com/Kaktushose/jda-commands/releases/latest).

### Maven

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

### Gradle

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

If you think that something is missing, and you want to add it yourself, feel free to open a pull request. Please try to
keep your code quality as good as mine and stick to the core concepts of this framework.

Special thanks to all contributors:

[![Contributors Display](https://badges.pufler.dev/contributors/kaktushose/jda-commands?size=50&padding=5&bots=false)](https://badges.pufler.dev)

## Dependencies

The following dependencies were used to build this framework:

- JDA
    - Version: 4.2.0_250
    - [Github](https://github.com/DV8FromTheWorld/JDA)
- Reflections
    - Version: 0.9.10
    - [Github](https://github.com/ronmamo/reflections)
- Gson
    - Version: 2.8.6
    - [Github](https://github.com/google/gson)
- slf4j-api
    - Version: 1.7.30
    - [Website](http://www.slf4j.org/)
- markdowngenerator
    - Version: 1.3.2
    - [Github](https://github.com/Steppschuh/Java-Markdown-Generator)
