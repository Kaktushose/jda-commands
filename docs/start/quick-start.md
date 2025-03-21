# Quick Start Guide
## Entrypoint

This is the easiest way of starting JDA-Commands. Besides your `JDA` (or `ShardManager`) instance, we also need a class
of the classpath to scan for interactions. 

=== "JDA"
    ```java
    public class Main {
            
        public static void main(String[] args) {
            JDA jda = yourJDABuilding();
            JDACommands.start(jda, Main.class);
        }
    }
    ```
=== "ShardManager"
    ```java
    public class Main {
    
        public static void main(String[] args) {
            ShardManager shardManager = yourShardManagerBuilding();
            JDACommands.start(shardManager, Main.class);
        }
    }
    ```

You can also pass specific packages to exclusively scan:
```java
JDACommands.start(jda, Main.class, "com.example.bot.commands");
```

### Builder
Some features of JDA-Commands require additional settings. While we provide default values for them, you can also start
JDA-Commands using a builder to fine tune some settings:

```java
JDACommands.builder(jda, Main.class)
        // configuration
        .start();
```

## Defining Interactions

You define interactions as methods. They are made up from the method annotations and in some cases the method signature, e.g. 
for command options.
These methods must be contained in a class annotated with [`@Interaction`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/annotations/interactions/Interaction.html).

```java
@Interaction
public class HelloWorld {

    @Command("greet")
    public void onCommand(CommandEvent event) {
        event.reply("Hello World!");
    }

}
```

The following interaction types are available:

- [`@Command`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/annotations/interactions/SlashCommand.html)
- [`@AutoComplete`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/annotations/interactions/AutoComplete.html)
- [`@Command`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/annotations/interactions/Command.html)
- [`@Button`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/annotations/interactions/Button.html)
- [`@StringSelectMenu`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/annotations/interactions/StringSelectMenu.html)
- [`@EntitySelectMenu`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/annotations/interactions/EntitySelectMenu.html)
- [`@Modal`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/annotations/interactions/Modal.html)

You can read more about the different interaction types [here](../interactions/overview.md).
