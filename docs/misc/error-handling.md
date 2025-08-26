# Error Handling

## Exceptions
JDA-Commands defines a set of custom runtime exceptions that can occur:
 
- [`ConfigurationException`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/exceptions/ConfigurationException.html) will be thrown if anything goes wrong while configuring JDA-Commands.
- [`InvalidDeclarationException`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/exceptions/InvalidDeclarationException.html) will be thrown if any errors are made in the declaration of interactions.
- [`InternalException`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/exceptions/InternalException.html) will be thrown if anything goes wrong internally. These errors should be [reported](https://github.com/Kaktushose/jda-commands/issues/new) to the devs.

If a [`ConfigurationException`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/exceptions/ConfigurationException.html)
or [`InvalidDeclarationException`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/exceptions/InvalidDeclarationException.html)
occurs during startup, JDA-Commands will shut down itself as well as JDA. To disable this behaviour, set [`JDACBuilder#shutdownJDA(boolean)`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/JDACBuilder.html#shutdownJDA(boolean))
to `false`. 

## Error Messages
As mentioned before, JDA-Commands has a set of error messages it uses all over the place. These messages include:

- Command Execution Failed Message (used for Exceptions)
- Constraint Failed Message (see [Validators](../middlewares/validator.md))
- Cooldown Message (see [Command Cooldown](../middlewares/cooldown.md))
- Insufficient Permissions Message (see [Permissions System](../middlewares/permissions.md))
- Timed Out Component Message (see [Runtime Concept](../start/runtime.md#components-and-modals))
- Type Adapting Failed Message (see [Type Adapters](../middlewares/typeadapter.md))

You can customize these error messages by providing an implementation of [`ErrorMessageFactory`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/embeds/error/ErrorMessageFactory.html).
You have to register it at the JDA-Commands Builder:
```java
JDACommands.builder(jda, Main.class)
    .errorMessageFactory(new OwnErrorMessageFactory());
    .start();
```
Or use the `@Implementation` annotation (requires the [Guice Extension](../di.md#implementation-annotation)):
```java
@Implementation
public class OwnErrorMessageFactory implements ErrorMessageFactory {...}
```

### JsonErrorMessageFactory
To make things easier, these error message can also be loaded from a JSON file. Therefore, you have to enable the 
[`JsonErrorMessageFactory`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/embeds/error/JsonErrorMessageFactory.html).
The `JsonErrorMessageFactory` takes an [`EmbedCache`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/embeds/EmbedCache.html) as input.

```java
JDACommands.builder(jda, Main.class)
    .errorMessageFactory(new JsonErrorMessageFactory(new EmbedCache("/path/to/json/embeds.json")));
    .start();
```
The template for the error messages can be found [here](https://github.com/Kaktushose/jda-commands/blob/main/core/src/examples/embeds.json).
The `{placeholders}` can be placed anywhere in the embeds and will get injected by JDA-Commands.  