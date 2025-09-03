# Dependency Injection
Dependency injection supports the dependency inversion principle by injecting dependencies into the class definitions 
instead of hardcoding them. You'll need dependency injection when using JDA-Commands, because  
most of your classes, like interaction controllers or [middlewares](./middlewares/overview.md) aren't instantiated by 
you anymore. 

JDA-Commands creates one instance of an interaction controller class per [runtime](interactions/overview.md#runtime-scoped-instances).
To allow the injection of own Objects in these instances, JDA-Commands provides an interface to integrate a dependency injection framework.

!!! tip
    Since JDA-Commands is a fundamental part of your discord bot and dependency injection is deeply connected with it,
    your bot should use a dependency injection framework for all your tasks.


## Default Dependency Injection Framework - Guice
!!! warning
    For information on how to use Google's Guice, please visit their [documentation](https://github.com/google/guice/wiki/). This wiki only covers the
    configuration part. 

If your using JDA-Commands via the `io.github.kaktushose:jda-commands:VERSION` artifact, an integration for 
[Google's Guice](https://github.com/google/guice) is shipped by default. 

To customize this integration you can pass an instance of [`GuiceExtensionData`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.extension.guice/com/github/kaktushose/jda/commands/guice/GuiceExtensionData.html)
to the JDA-Commands builder, which allows you to provide an own instance of [Guice's Injector](https://google.github.io/guice/api-docs/7.0.0/javadoc/com/google/inject/Injector.html).

!!! example "Configuring Guice"
    ```java
    Injector yourInjector = Guice.createInjector(new YourOwnGuiceModule());

    JDACommands.builder(jda, Main.class)
            .extensionData(new GuiceExtensionData(yourInjector))
            .start();
    ```

!!! tip "JDA Object"
    The `JDA` instance is provided by JDA-Commands and can be obtained via Guice.
    ```java
    @Interaction
    public class GreetCommand {
        
        private final JDA jda;

        @Inject
        public GreetCommand(JDA jda) {
            this.jda = jda;
        }

        @Command("greet")
        public void onCommand(CommandEvent event) { ... }
    }
    ```

## `@Implementation` annotation
JDA-Commands has many interfaces to customize specific framework behaviour. 
If you're using the Guice integration you can benefit from the convenient [`@Implementation`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.extension.guice/com/github/kaktushose/jda/commands/guice/Implementation.html)
annotation. 

This annotation allows the automatic instantiation and registration for implementations of following interfaces:

!!! note inline end
    If you're annotating an implementation of `Middleware`, `Validator` or `TypeAdapter` you have to provide additional configuration via the `@Implementation` annotation.

- [PermissionsProvider](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/permissions/PermissionsProvider.html)
- [GuildScopeProvider](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/scope/GuildScopeProvider.html)
- [ErrorMessageFactory](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/embeds/error/ErrorMessageFactory.html)
- [Descriptor](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/definitions/description/Descriptor.html)

- [Middleware](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/dispatching/middleware/Middleware.html) + `@Implementation#priority()` set
- [Validator](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/dispatching/validation/Validator.html) + `@Implementation#annotation()` set
- [TypeAdapter](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/dispatching/adapter/TypeAdapter.html) + `@Implemenetation#clazz()` set

The annotated classes will be instantiated with help of `com.google.inject.Injector` similar to interaction controllers.

!!! example
    ```java
    @Implementation
    public class CustomGuildScopeProvider implements GuildScopeProvider {

        private final Database database;
    
        @Inject
        public HelloWord(Database database) {
            this.database = database;
        }

        public Set<Long> apply(CommandData data) { 
            return database.getGuildsForCommand(data);
        }
    }

    ```

## Custom dependency injection integrations
If you want to integrate another dependency injection framework, you have to provide your own 
implementation of [InteractionControllerInstantiator](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/dispatching/instance/InteractionControllerInstantiator.html).

You can do this by either passing it to the builder or by creating your own [extension](extension/writing.md).

!!! example
    ```java
    JDACommands.builder(jda, Main.class)
            .instanceProvider(new OwnInterationControllerInstantiator(someContext))
            .start();
    ```