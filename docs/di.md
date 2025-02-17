# Overview
As noted in [here](interactions/overview.md#runtime-scoped-instances) JDA-Commands creates one instance of an
interaction controller class per runtime. To allow the injection of own Objects in these instances, JDA-Commands provides
an interface to integrate a dependency injection framework.

!!! note
    Since JDA-Commands is a fundamental part of your discord bot and dependency injection is deeply connected with it,
    your bot should use a dependency injection framework for all your tasks.


## Default Dependency Injection Framework - Guice
If your using JDA-Commands via the "io.github.kaktushose:jda-commands:<version>" artifact, an integration for 
[Google's Guice](https://github.com/google/guice) is shipped by default. 

To customize this integration you can pass an instance of [GuiceExtensionData](https://kaktushose.github.io/jda-commands/javadocs/latest/io.github.kaktushose.jda.commands.extension.guice/com/github/kaktushose/jda/commands/guice/GuiceExtensionData.html)
to the JDA-Commands builder, which allows you to provide an own instance of [Guice's Injector](https://google.github.io/guice/api-docs/7.0.0/javadoc/com/google/inject/Injector.html) 

!!! example
    ```java
    Injector yourInjector = Guice.createInjector(new YourOwnGuiceModule());

    JDACommands.builder(jda, Main.class)
            .extensionData(new GuiceExtensionData(yourInjector))
            .start();
    ```

For further information on how to configure Google's Guice, please visit their [documentation](https://github.com/google/guice/wiki/).

### JDA Object
Since each interaction controller class instance is bound to one [conversation](interactions/overview.md#runtime-scoped-instances) and thus one guild, 
the corresponding `JDA` instance can be obtained via Guice.

!!! example
    ```java
    @Interaction
    public class HelloWorld {
        private final JDA jda;

        @Inject
        public HelloWord(JDA jda) {
            this.jda = jda;
        }

        @SlashCommand("greet")
        public void onCommand(CommandEvent event) { ... }

    }
    ```

### The `@Implementation` annotation
JDA-Commands has many interfaces to customize specific framework behaviour. 
If you're using the Guice integration you can benefit from the convenient [@Implementation](https://kaktushose.github.io/jda-commands/javadocs/latest/io.github.kaktushose.jda.commands.extension.guice/com/github/kaktushose/jda/commands/guice/Implementation.html)
annotation. 

This annotation allows the automatic instantiation and registration for implementations of following interfaces:

- [PermissionsProvider](https://kaktushose.github.io/jda-commands/javadocs/development/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/permissions/PermissionsProvider.html)
- [GuildScopeProvider](https://kaktushose.github.io/jda-commands/javadocs/development/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/scope/GuildScopeProvider.html)
- [ErrorMessageFactory](https://kaktushose.github.io/jda-commands/javadocs/development/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/embeds/error/ErrorMessageFactory.html)
- [Descriptor](https://kaktushose.github.io/jda-commands/javadocs/development/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/definitions/description/Descriptor.html)

- [Middleware](https://kaktushose.github.io/jda-commands/javadocs/development/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/dispatching/middleware/Middleware.html) + `@Implementation#priority()` set
- [Validator](https://kaktushose.github.io/jda-commands/javadocs/development/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/dispatching/validation/Validator.html) + `@Implementation#annotation()` set
- [TypeAdapter](https://kaktushose.github.io/jda-commands/javadocs/development/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/dispatching/adapter/TypeAdapter.html) + `@Implemenetation#clazz()` set

!!! info
    If you're annotating an implementation of `Middleware`, `Validator` or `TypeAdapter` you have to provide additional configuration via the `@Implementation` annotation.

The annotated classes will be instantiated with help of `com.google.inject.Injector` similar to interaction controllers.

!!! example
    ```java
    @Implementation
    public class CustomGuildScopeProvider implements GuildScopeProvider {

        Set<Long> apply(CommandData data) { ... }
    }

    ```

## Custom dependency injection integrations
If you want to integrate another dependency injection framework, you have to provide an own 
implementation of [InteractionControllerInstantiator](https://kaktushose.github.io/jda-commands/javadocs/latest/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/dispatching/instance/InteractionControllerInstantiator.html)

You can do this by either passing it to the builder or by creating your own [extension](extensions.md).

!!! example
    ```java
    JDACommands.builder(jda, Main.class)
            .instanceProvider(new OwnInterationControllerInstantiator(someContext))
            .start();
    ```