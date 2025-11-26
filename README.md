[![JDA-Version](https://img.shields.io/badge/JDA%20Version-5.5.1-important)](https://github.com/DV8FromTheWorld/JDA#download)
[![Release badge](https://release-badges-generator.vercel.app/api/releases.svg?user=kaktushose&repo=jda-commands&gradient=92e236,92e236)](https://github.com/Kaktushose/proteus/releases/latest)
[![Source build & test](https://github.com/Kaktushose/jda-commands/actions/workflows/build_ci.yml/badge.svg)](https://github.com/Kaktushose/jda-commands/actions/workflows/build_ci.yml)
[![Release Deployment](https://github.com/Kaktushose/jda-commands/actions/workflows/cd.yml/badge.svg)](https://github.com/Kaktushose/jda-commands/actions/workflows/cd.yml)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/f2b4367f6d0f42d89b7e51331f3ce299)](https://app.codacy.com/gh/Kaktushose/jda-commands/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![license-shield](https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg)]()

# JDA-Commands

A lightweight, easy-to-use command framework for building Discord bots
with [JDA](https://github.com/DV8FromTheWorld/JDA) with full support for interactions. JDA-Commands goal is to remove
any boilerplate code, so you can focus solely on the business logic of your bot - writing bots has never been easier!

## Features

- Simple and intuitive syntax following an annotation-driven and declarative style


- Built-in support for slash commands, components, embeds, context menus and modals


- Automatic and customizable type adapting and constraint validation of parameters


- Expandable executing chain (Middleware API)


- Multithreaded event handling using VirtualThreads


- First-hand localization support (via [project fluent](https://projectfluent.org/) or custom implementation)

- Usage of [jSpecify](https://jspecify.dev/) nullability annotations

- and many more!

If you want to learn more, go check out the [Wiki](https://kaktushose.github.io/jda-commands/wiki/) or [Javadocs](https://kaktushose.github.io/jda-commands/javadocs/latest/) and join our [Discord server](https://discord.gg/tmq9BrZEKb).

## Example

```java
@Interaction
public class CookieClicker {

    private int counter;

    @Command(value = "cookie clicker", desc = "Play cookie clicker")
    public void onClicker(CommandEvent event) {
        event.with().components("onCookie", "onReset").reply("You've got { $count } cookie(s)!", entry("count", counter));
    }

    @Button(value = "Collect", emoji = "🍪", style = ButtonStyle.SUCCESS)
    public void onCookie(ComponentEvent event) {
        event.reply("You've got { $count } cookie(s)!", entry("count", counter++));
    }

    @Button(value = "Reset", emoji = "🔄", style = ButtonStyle.DANGER)
    public void onReset(ComponentEvent event) {
        count = 0;
        event.reply("You've got { $count } cookie(s)!", entry("count", counter));
    }
}
```

---

## Download

You can download the latest version [here](https://github.com/Kaktushose/jda-commands/releases/latest).
### Maven
```xml
<dependency>
   <groupId>io.github.kaktushose</groupId>
   <artifactId>jda-commands</artifactId>
   <version>4.0.0</version>
</dependency>
```

### Gradle
```groovy
repositories {
   mavenCentral()
}
dependencies {
   implementation("io.github.kaktushose:jda-commands:4.0.0")
}
```

## Contributing

If you think that something is missing, and you want to add it yourself, feel free to open a pull request. Please consider opening an issue
first, so we can discuss if your changes fit to the framework. Also check out the [project board](https://github.com/users/Kaktushose/projects/1)
to see what we already planned for future releases.

Special thanks to all contributors, especially to [@Goldmensch](https://github.com/Goldmensch) and [@lus](https://github.com/lus) <3

<a href = "https://github.com/kaktushose/jda-commands/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=kaktushose/jda-commands" alt="contributors"/>
</a>