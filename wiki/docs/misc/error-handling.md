# Error Handling

## Exceptions
JDA-Commands defines a set of custom runtime exceptions that can occur:
 
- <io.github.kaktushose.jdac.exceptions.ConfigurationException> will be thrown if anything goes wrong while configuring JDA-Commands
- <io.github.kaktushose.jdac.exceptions.InvalidDeclarationException> will be thrown if any errors are made in the declaration of interactions
- <io.github.kaktushose.jdac.exceptions.ParsingException> will be thrown if the JSON parsing of the [Embed API](../message/embeds.md) fails
- <io.github.kaktushose.jdac.exceptions.InternalException> will be thrown if anything goes wrong internally. These errors should be [reported](https://github.com/Kaktushose/jda-commands/issues/new) to the devs

If a <io.github.kaktushose.jdac.exceptions.ConfigurationException>
or <io.github.kaktushose.jdac.exceptions.InvalidDeclarationException>
occurs during startup, JDA-Commands will shut down itself as well as JDA. To disable this behaviour, set <JDACBuilder#shutdownJDA(boolean)>
to `false`. 

## Error Messages
As mentioned before, JDA-Commands has a set of error messages it uses all over the place. These messages include:

- Command Execution Failed Message (used for Exceptions)
- Constraint Failed Message (see [Validators](../middlewares/validator.md))
- Cooldown Message (see [Command Cooldown](../middlewares/cooldown.md))
- Insufficient Permissions Message (see [Permissions System](../middlewares/permissions.md))
- Timed Out Component Message (see [Runtime Concept](../start/runtime.md#components-and-modals))
- Type Adapting Failed Message (see [Type Adapters](../middlewares/typeadapter.md))

You can customize these error messages by providing an implementation of <ErrorMessageFactory> either at the builder or 
by annotating it with <io.github.kaktushose.jdac.guice.Implementation>.

!!! example
    === "`@Implementation`"
        ```java
        @Implementation
        public class OwnErrorMessageFactory implements ErrorMessageFactory {...}
        ```
    === "Builder Registration"
        ```java
        JDACommands.builder(jda, Main.class)
            .errorMessageFactory(new OwnErrorMessageFactory());
            .start();
        ```

To make things easier, these error messages can also be loaded from a `JSON` source using the [Embed API](../message/embeds.md). 
Therefore, you have to pass an <EmbedDataSource> to the <EmbedConfig>.
```java
JDACommands.builder(jda, Main.class)
    .embeds(config -> config.errorSource(EmbedDataSource.file(Path.of("errorEmbeds.json")))
    .start();
```
The template for the error messages can be found [here](https://github.com/Kaktushose/jda-commands/blob/main/core/src/examples/embeds.json).
The `{ $placeholders }` can be placed anywhere in the embeds and will get injected by JDA-Commands.  