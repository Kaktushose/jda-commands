[![JDA-Version](https://img.shields.io/badge/JDA%20Version-5.0.0--beta.17-important)](https://github.com/DV8FromTheWorld/JDA#download)
[![Generic badge](https://img.shields.io/badge/Download-4.0.0--alpha.5-green.svg)](https://github.com/Kaktushose/jda-commands/releases/latest)
[![Java CI](https://github.com/Kaktushose/jda-commands/actions/workflows/ci.yml/badge.svg?branch=dev)](https://github.com/Kaktushose/jda-commands/actions/workflows/ci.yml)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/f2b4367f6d0f42d89b7e51331f3ce299)](https://app.codacy.com/gh/Kaktushose/jda-commands/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![Codacy Badge](https://app.codacy.com/project/badge/Coverage/f2b4367f6d0f42d89b7e51331f3ce299)](https://app.codacy.com/gh/Kaktushose/jda-commands/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_coverage)
[![license-shield](https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg)]()

# JDA-Commands

A lightweight, easy-to-use command framework for building Discord bots
with [JDA](https://github.com/DV8FromTheWorld/JDA) with full support for interactions. JDA-Commands goal is to remove
any boilerplate code, so you can focus solely on the business logic of your bot - writing bots has never been easier!

### Version Overview

| jda-commands | JDA | Text Commands | Interactions | Stable |
|-----------------------------------------------------------------------------|-|--|---|---|
| [4.0.0-alpha.5](https://github.com/Kaktushose/jda-commands/releases/latest) |5|❌|✅|❌|
| [3.0.0](https://github.com/Kaktushose/jda-commands/releases/tag/v3.0.0)     |5|✅|❌|✅|
| [2.2.0](https://github.com/Kaktushose/jda-commands/releases/tag/v.2.0.0)    |4|✅|❌|✅|

## Features

- Simple and intuitive syntax following an annotation-driven and declarative style

- Built-in support for slash commands, components, context menus and modals

- Type adapting of parameters

- Expandable execution chain including type adapters, filters, permissions and constraints

- Built-in support for ephemeral replies, permissions, localization

---

The following example will demonstrate how easy it is to write commands:

Let's rebuild the official slash commands example from
the [JDA Readme](https://github.com/DV8FromTheWorld/JDA#listening-to-events) using jda-commands:

```java
@Interaction
public class SlashCommandExample {

    @SlashCommand(value = "ping", desc = "Calculate ping of the bot")
    public void onPing(CommandEvent event) {
        long time = System.currentTimeMillis();
        event.reply("Pong!", success -> event.reply("Pong: %d ms", System.currentTimeMillis() - time));
    }

    @Permissions("BAN_MEMBERS")
    @SlashCommand(value = "ban", enabledFor = Permission.BAN_MEMBERS, desc = "Bans a user", ephemeral = true)
    public void onBan(CommandEvent event, @Param("The member to ban") Member target, @Optional("no reason") @Param("The ban reason") String reason) {
        event.getGuild().ban(target, 0, TimeUnit.SECONDS).reason(reason).queue(
                success -> event.reply("**%s** was banned by **%s**", target.getAsMention(), event.getUser().getAsMention()),
                error -> event.reply("Some error occurred, try again!")
        );
    }
}
```

Finally, start the framework by calling:

```java
JDACommands.start(jda,Main.class,"com.package");
```

---

You can find a detailed list of all features down below _(click on the ▶ for details)_:

### Execution

<details>
<summary>Request-scoped Instances</summary>

For every command execution a new instance of the controller class is created. Subsequent executions of components are
executed in the same instance.
This allows you to store stateful objects, like the target of a ban command, _inside_ the controller class.

</details>

<details>
<summary>Private Channel Support</summary>

If enabled, commands can also be executed in direct messages.

</details>

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

![embed](https://cdn.discordapp.com/attachments/545967082253189121/938871716749377586/Untitled.png)

</details>

### Constraints

<details>
<summary>Permissions System</summary>

Besides the default permissions system of slash commands, this framework comes in with an own system, supporting both
discord and custom permissions. By default, you can use all
permissions defined inside
JDAs [Permission Embed](https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/Permission.html). By adding your own
permission validator, you can use custom permission strings and bind permissions to certain roles or members.

</details>

<details>
<summary>Filter Chain</summary>

You can define filters that will run before each command execution. This can be useful to perform additional checks,
which aren't supported by this framework by default.

</details>

<details>
<summary>Cooldown System</summary>

Commands can have a per-user cooldown to rate limit the execution of commands.

</details>

### Misc

<details>
<summary>Error Messages</summary>

There are default error embeds for all validation systems of this framework, i.e. parameter constraints, permissions,
etc.

</details>

<details>
<summary>Localization</summary>

This framework supports the use of
JDAs [LocalizationFunction](https://ci.dv8tion.net/job/JDA5/javadoc/net/dv8tion/jda/api/interactions/commands/localization/LocalizationFunction.html)
for localizing slash commands.

Furthermore, you can adapt the auto generated bot responses. All embeds
sent can also be loaded from a json file, which uses
placeholders. _[example](https://github.com/Kaktushose/jda-commands/blob/master/src/examples/embeds.json)_

</details>

<details>
<summary>Embed Deserialization</summary>

You can serialize and deserialize JDAs EmbedBuilder object to json. This comes in pretty handy, because for example you
don't have to recompile the whole project if you find one typo inside your
embed. _[example](https://github.com/Kaktushose/jda-commands/blob/master/src/examples/embeds.json)_

</details>

<details>
<summary>Dependency Injection</summary>

This framework has a basic implementation of dependency injection, since you don't construct your command classes on
your own.

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
    <groupId>com.github.kaktushose</groupId>
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
    implementation 'com.github.kaktushose:jda-commands:VERSION'
}
```

## Contributing

If you think that something is missing, and you want to add it yourself, feel free to open a pull request. Please try to
keep your code quality as good as mine and stick to the core concepts of this framework.

Special thanks to all contributors <3
