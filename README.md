[![JDA-Version](https://img.shields.io/badge/JDA%20Version-5.2.2-important)](https://github.com/DV8FromTheWorld/JDA#download)
[![Generic badge](https://img.shields.io/badge/Download-4.0.0--beta.4-green.svg)](https://github.com/Kaktushose/jda-commands/releases/latest)
[![Java CI](https://github.com/Kaktushose/jda-commands/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/Kaktushose/jda-commands/actions/workflows/ci.yml)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/f2b4367f6d0f42d89b7e51331f3ce299)](https://app.codacy.com/gh/Kaktushose/jda-commands/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![license-shield](https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg)]()

# JDA-Commands

A lightweight, easy-to-use command framework for building Discord bots
with [JDA](https://github.com/DV8FromTheWorld/JDA) with full support for interactions. JDA-Commands goal is to remove
any boilerplate code, so you can focus solely on the business logic of your bot - writing bots has never been easier!

### Version Guide

| jda-commands                                                               | JDA | Text Commands | Interactions |
|----------------------------------------------------------------------------|-----|---------------|--------------|
| [4.0.0-beta.4](https://github.com/Kaktushose/jda-commands/releases/latest) | 5   | ❌             | ✅            |
| [3.0.0](https://github.com/Kaktushose/jda-commands/releases/tag/v3.0.0)    | 5   | ✅             | ❌            |
| [2.2.0](https://github.com/Kaktushose/jda-commands/releases/tag/v.2.0.0)   | 4   | ✅             | ❌            |

## Features

- Simple and intuitive syntax following an annotation-driven and declarative style


- Built-in support for slash commands, components, context menus and modals


- Automatic and customizable type adapting and constraint validation of parameters


- Expandable executing chain (Middleware API)


- Multithreaded event handling using VirtualThreads

---

The following example will demonstrate how easy it is not only to write commands, but also to integrate components with them:

```java
@Interaction
public class CookieClicker {

    private int count;

    @SlashCommand(value = "cookie clicker", desc = "Play cookie clicker")
    public void onClicker(CommandEvent event) {
        event.with().components("onCookie", "onReset").reply("You've got %s cookie(s)!", count);
    }

    @Button(value = "Collect", emoji = "🍪", style = ButtonStyle.SUCCESS)
    public void onCookie(ComponentEvent event) {
        count++;
        event.reply("You've got %s cookie(s)!", count);
    }

    @Button(value = "Reset", emoji = "🔄", style = ButtonStyle.DANGER)
    public void onReset(ComponentEvent event) {
        count = 0;
        event.reply("You've got %s cookie(s)!", count);
    }
}
```

Finally, start the framework by calling:

```java
JDACommands.start(jda, Main.class);
```

---

If you want to learn more, check out the [Wiki](https://kaktushose.github.io/jda-commands/wiki/) or
the [Javadocs](https://kaktushose.github.io/jda-commands/javadocs/latest/).

## Download

You can download the latest version [here](https://github.com/Kaktushose/jda-commands/releases/latest).
### Maven
```xml
<dependency>
   <groupId>io.github.kaktushose</groupId>
   <artifactId>jda-commands</artifactId>
   <version>4.0.0-beta.4</version>
</dependency>
```

### Gradle
```groovy
repositories {
   mavenCentral()
}
dependencies {
   implementation("io.github.kaktushose:jda-commands:4.0.0-beta.4")
}
```

## Contributing

If you think that something is missing, and you want to add it yourself, feel free to open a pull request. Please try to
keep your code quality as good as mine and stick to the core concepts of this framework. Also consider opening an issue
first, so we can discuss if your changes fit to the framework.

Special thanks to all contributors, especially to @Goldmensch and @lus <3

[![Contributors Display](https://badges.pufler.dev/contributors/Kaktushose/jda-commands?size=50&padding=5&perRow=10&bots=false)]([https://badges.pufler.dev](https://github.com/Kaktushose/jda-commands/graphs/contributors))
