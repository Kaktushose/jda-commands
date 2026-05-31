# Slash Commands
!!! info
    If you're new to JDA and Discord Bots in general, please make yourself familiar with the
    [JDA wiki](https://jda.wiki/using-jda/interactions/) first. We assume that the basic structure of interactions is known.

SlashCommands are defined by annotating a method with <io.github.kaktushose.jdac.annotations.interactions.Command>.
The first parameter must always be a <CommandEvent>.
The name and other metadata of the command is passed to the annotation.
```java
@Command(value = "example", desc = "This is an example command")
public void onCommand(CommandEvent event) {...}
```

## Sub Commands & Sub Command Groups
In contrast to JDA, JDA-Commands doesn't differentiate between slash commands, sub command groups and sub commands.
JDA-Commands determines the type automatically based on the command names.

Let's say we have the following commands in our moderation bot:
```java
@Command("delete")
public void onDeleteMessages(CommandEvent event) {...}

@Command("moderation warn")
public void onWarnMember(CommandEvent event) {...}

@Command("moderation kick")
public void onKickMember(CommandEvent event) {...}

@Command("moderation ban")
public void onBanMember(CommandEvent event) {...}
```
JDA-Commands will create a tree structure of these commands. A depth-first-search is then performed to determine which
commands should be registered as a slash command, a sub command or a sub command group.
```
├── delete
└── moderation
    ├── warn
    ├── kick
    └── ban
```
??? tip "Debugging"
    JDA-Commands will log this tree on log-level `DEBUG`. This might help you with debugging, for example when a command
    doesn't show up.

In our example the following commands will be registered:

- `/delete`
- `/moderation warn`
- `/moderation kick`
- `/moderation ban`

To simplify things, you can also use the <io.github.kaktushose.jdac.annotations.interactions.Interaction>
to add a base name to all slash commands in a command controller:
```java
@Interaction("moderation")
public class ModerationCommands {
    
    @Command("warn")
    public void onWarnMember(CommandEvent event) {...}

    @Command("kick")
    public void onKickMember(CommandEvent event) {...}

    @Command("ban")
    public void onBanMember(CommandEvent event) {...}
}
```

## Command Options
You can add command options by simply adding a parameter to the method.
```java
@Command("ban")
public void onBanMember(CommandEvent event, Member target, String reason, int delDays) {
    (...)
}
```
JDA-Commands will attempt to type adapt the command options. You can find a concrete list of all supported type adapters
[here](../../middlewares/typeadapter.md#default-type-adapters).

You can also [register your own type adapters](../../middlewares/typeadapter.md).

### OptionType
The parameters will automatically be mapped to the best fitting <OptionType>,
defaulting to <OptionType#STRING>. You can override this mapping by using the
<io.github.kaktushose.jdac.annotations.interactions.Param> annotation.
```java
@Command("ban")
public void onBanMember(CommandEvent event, @Param(type = OptionType.USER) IMentionable target) {
    (...)
}
```

### Name & Description
Use the <Param> annotation to set a name and a description
for a command option. By default, the parameter name will be used as the option name.
```java
@Command("ban")
public void onBanMember(CommandEvent event, 
                        @Param("The member to ban") Member target,
                        @Param("The reason to ban the member") String reason,
                        @Param(name = "deletion days", value = "The number of days to delete messages for") int delDays) {
    (...)
}
```
---
!!! danger inline end
    In order for JDA-Commands to use the parameter name as the command option name, you must enable the `-parameters`
    compiler flag.

=== "Maven"
    ```xml title="pom.xml"
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <configuration>
            <compilerArgs>
                <compilerArg>-parameters</compilerArg>
            </compilerArgs>
        </configuration>
    </plugin>
    ```

=== "Gradle (Kotlin DSL)"
    ```kotlin title="build.gradle.kts"
    tasks.withType<JavaCompile> {
        options.compilerArgs += "-parameters"
    }
    ```

=== "Gradle (Groovy DSL)"
    ```groovy title="build.gradle"
    compileJava {
        options.compilerArgs << '-parameters'
    }
    ```

=== "IntelliJ"    
    If you compile your project with IntelliJ during development go to `Settings > Compiler > Java Compiler`
    and add the `-parameters` flag:

    ![IntelliJ Settings](../../assets/intellij.png)
---

### Optional
In order to make a command option optional, annotate the parameter with <Param>.
```java
@Command("ban")
public void onBanMember(CommandEvent event, Member target, @Param(optional = true) String reason, @Param(optional = true) int delDays) {
    (...)
}
```

Alternatively, you can wrap the parameter in an <Optional>.
```java
@Command("ban")
public void onBanMember(CommandEvent event, Member target, Optional<String> reason, Optional<Integer> delDays) {
    (...)
}
```

!!! note
    Required options must be added before non-required options.

### Min & Max Value
Use the <Min> or <Max> annotation to set the minimum and maximum value for numeral options.
!!! example
    ```java
    @Command("ban")
    public void onBanMember(CommandEvent event, Member target, String reason, @Max(7) int delDays) {
        (...)
    }
    ```

## Choices
Use the <Choices> annotation to add choices to a command option:
```java
@Command("ban")
public void onBanMember(CommandEvent event, 
                        Member target, 
                        @Choices({"Harassment", "Scam", "Advertising"}) String reason, 
                        int delDays) {
    (...)
}
```
The example above will use the given String for both the `name` and the `value`. You can use the `name:value` format to
specify both:
```java
@Command("ban")
public void onBanMember(CommandEvent event, 
                        Member target, 
                        @Choices({"Harassment:reason_1", "Scam:reason_2", "Advertising::reason_3"}) String reason, 
                        int delDays) {
    (...)
}
```

The examples above all provide the choices statically via the annotation value. However, if needed, choices can also be provided by a public static
method returning `List<String>`.
```java
public void onCommand(CommandEvent event, @Choices(provider = "getChoices") String option) {...}

public static List<String> getChoices() {
 return List.of("Apple", "Banana", "Cherry");
}
```
If both static values and a provider is present, the values will be combined.

!!! tip
    Providers can also be defined in a different class than the command:
    ```java
    public void onCommand(CommandEvent event, @Choices(source = Other.class, provider = "getChoices") String option) {...}
    ```

**Dependency Injection**

This static provider method also supports dependency injection via the [Guice Extension](../../di.md#default-dependency-injection-framework-guice).
```java
public static List<String> getChoices(MyChoiceProvider provider) {
 return provider.getChoices();
}
```

Injectable are all types listed [here](../../di.md#interaction-controller-classes) except <JDA>.
The scope is set to <JDACScope#INITIALIZED>.

!!! note
    If the provider method is overloaded, all provider methods will be called and combined.

## Auto Complete
You can add auto complete to a command option by defining an auto complete handler for it by annotating a method with
<AutoComplete>. Auto Complete handlers are always bound to
one or more slash commands.

The slash commands can either be referenced by the:

1. Command Name

    If referenced by the command name, the handler will handle any command whose name starts with the given name:
    
    !!! example
        ```java
        @Command("favourite fruit")
        public void fruitCommand(CommandEvent event, String fruit) {
            event.reply("You've chosen: %s", fruit);
        }
        
        @Command("favourite vegetable")
        public void vegetableCommand(CommandEvent event, String vegetable) {
            event.reply("You've chosen: %s", vegetable);
        }
        
        @AutoComplete("favourite") //(1)!
        public void onFavouriteAutoComplete(AutoCompleteEvent event) {
            event.replyChoices(...);
        }
        ```
    
        1. This auto complete handler will receive auto complete events for both `/favourite fruit` and `/favourite vegetable`
    
    It is also possible to reference the commands by their full name:
    !!! example
        ```java
        @AutoComplete({"favourite fruit", "favourite vegtable"})
        public void onFavouriteAutoComplete(AutoCompleteEvent event) {
            event.replyChoices(...);
        }
        ```

2. Method Name
            
    If referenced by the method name the handler will only handle the slash command of the given method:
    
    !!! example
        ```java
        @Command("favourite fruit")
        public void fruitCommand(CommandEvent event, String fruit) {
            event.reply("You've chosen: %s", fruit);
        }
        
        @AutoComplete("fruitCommand") //(1)!
        public void onFruitAutoComplete(AutoCompleteEvent event) {
            event.replyChoices(...);
        }    
        ```
    
        1. This auto complete handler will **only** receive auto complete events for `/favourite fruit`!

!!! warning
    If an auto complete handler doesn't specify any command options, it will be registered implicitly for every command
    option of the given slash command(s)!

So far we haven't specified which command options should have auto complete, resulting in every command option having
auto complete enabled. If you want to avoid that, you have to explicitly state the command options the handler supports:

!!! example
    ```java
    @Command("favourite food")
    public void foodCommand(CommandEvent event, String fruit, String vegetable) {
        event.reply("You've chosen: %s and %s".formatted(fruit, vegetable));
    }

    @AutoComplete(vale = "foodCommand", options = "fruit")
    public void onFruitAutoComplete(AutoCompleteEvent event) {
        event.replyChoices(...);
    }
    ```

You can have multiple auto complete handler for the same slash command, but each command option can only have exactly
one handler. An auto complete handler that explicitly supports a command option will always be called over a handler
that is implicitly registered.

