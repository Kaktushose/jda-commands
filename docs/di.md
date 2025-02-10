# Overview
As noted in [here](interactions/overview.md#runtime-scoped-instances) JDA-Commands creates one instance of an
interaction controller class per runtime. To allow the injection of own Objects in these instances, JDA-Commands provides
an interface to integrate a dependency injection framework.

## Default Dependency Injection Framework - Guice
If your using JDA-Commands via the "io.github.kaktushose:jda-commands:<version>" artifact, an integration for 
[Google's Guice](https://github.com/google/guice) is shipped by default. 

To customize this integration you can provide an instance of `io.github.kaktushose.jda.commands.guice.GuiceExtensionData`,
which allows you to provide an own instance of `com.google.inject.Injector`. 
Please visit the Guice Wiki for further information on how to customize Google Guice.

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

## Custom dependency injection integrations
If you want to integrate another dependency injection framework, you have to provide an own 
implementation of `com.github.kaktushose.jda.commands.dispatching.instance.InteractionControllerInstantiator`.