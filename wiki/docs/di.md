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
    For information on how to use Googles Guice, please visit their [documentation](https://github.com/google/guice/wiki/). This wiki only covers the
    configuration part. 

If your using JDA-Commands via the `io.github.kaktushose:jda-commands:VERSION` artifact, an integration for 
[Googles Guice](https://github.com/google/guice) is shipped by default. 

### Interaction controller classes

You can annotate the constructor (or fields) of your interaction controller class with <jakarta -> Inject>
to either inject [framework components (properties)](misc/property.md) or your own 
objects provided by a custom <guice -> Injector>.

You can directly inject following classes:

- <JDACommands>
- <Definitions>
- <JDA> (the instance bound to this interaction)
- <I18n>
- <MessageResolver>
- <EmojiResolver>
- <PlaceholderResolver>
- <io.github.kaktushose.jdac.definitions.description.Descriptor>
- <ClassFinder>
- <Introspection> (see [here fore more information](misc/introspection.md))

If you need any other <Property>, just inject them via the <Introspection> instance manually.
During instantiation (inside the constructor) of an interaction controller class, the stage is set to <Stage#RUNTIME>.

!!! example 
    ```java
    @Interaction
    class MyCommand {
        
        @Inject
        MyCommand(JDA jda, //(1)
                  MessageResolver resolver, //(2)
                  Introspection introspection) { //(3)
            // do whatever you want with them
        }
        
        @Command("hello")
        public void onCommand(CommandEvent event) {
            ...
        }
    }
    ```
    
    1. the <JDA> instance used. If you're using <ShardManager>, this is the one the guild executing the command is paired to.
    2. the <MessageResolver> instance used by JDA-Commands. It's a "framework component".
    3. the <Introspection> instance used in this scope with stage set to <Stage#RUNTIME>

### Configuration (providing a custom <guice -> Injector>)

To customize this integration you can pass an instance of <GuiceExtensionData>
to the JDA-Commands builder, which allows you to provide an own instance of <guice -> Injector>.

!!! example "Configuring Guice"
    ```java
    Injector yourInjector = Guice.createInjector(new YourOwnGuiceModule());

    JDACommands.builder(jda, Main.class)
            .extensionData(new GuiceExtensionData(yourInjector))
            .start();
    ```

## `@Implementation` annotation
JDA-Commands has many interfaces to customize specific framework behaviour. 
If you're using the Guice integration you can benefit from the convenient <guice.Implementation>
annotation. 

This annotation allows the automatic instantiation and registration for implementations of following interfaces:

!!! note inline end
    If you're annotating an implementation of `Middleware`, `Validator` or `TypeAdapter` you have to provide additional configuration via the `@Implementation` annotation.

- <PermissionsProvider>
- <GuildScopeProvider>
- <ErrorMessageFactory>
- <io.github.kaktushose.jdac.definitions.description.Descriptor>

- <io.github.kaktushose.jdac.dispatching.middleware.Middleware> + <Implementation.Middleware#priority()> set
- <io.github.kaktushose.jdac.dispatching.validation.Validator> + <Implementation.Validator#annotation()> set
- <io.github.kaktushose.jdac.dispatching.adapter.TypeAdapter> + <Implementation.TypeAdapter#source()> & <Implementation.TypeAdapter#target()> set

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

Just like in [interaction controller methods](#interaction-controller-classes), you can
inject framework components and custom objects in your classes.
Take a look at the Javadocs of <Implementation> to know what you can inject here.

## Custom dependency injection integrations
If you want to integrate another dependency injection framework, you have to provide your own 
implementation of <InteractionControllerInstantiator>.

You can do this by either passing it to the builder or by creating your own [extension](misc/extension/writing.md).

!!! example
    ```java
    JDACommands.builder(jda, Main.class)
            .instanceProvider(new OwnInterationControllerInstantiator(someContext))
            .start();
    ```