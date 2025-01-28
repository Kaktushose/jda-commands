# JDACommands Guice Extension
This Extension to [JDACommands](https://github.com/Kaktushose/jda-commands) allows the use of [Google Guice](https://github.com/google/guice)
as a dependency injection framework by providing an own implementation of 
[InteractionClassInstantiator](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/dispatching/instance/InteractionClassProvider.html)

### Usage
To use this extension just add it to your `build.gradle.kts`, JDACommands will then pick it up automatically.

### Provide custom Injector
If you want to use a custom [Guice Injector](https://google.github.io/guice/api-docs/7.0.0/javadoc/com/google/inject/Injector.html),
you can provide one by using [`GuiceExtensionData`](jda.commands.guice/com/github/kaktushose/jda/commands/guice/GuiceExtensionData.html).

```java
import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.guice.GuiceExtensionData;
import com.google.inject.Guice;

...
Injector custom = Guice.createInjector();

JDACommands jdaCommands = JDACommands.builder(jda, Main.class)
        .extensionData(new GuiceExtensionData(custom))
        .start();
...
```

### Automatically discovered implementations
This JDACommands Extension allows the automatic discovering of implementations of following interfaces:
- [Middleware](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/dispatching/middleware/Middleware.html)
- [Validator](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/dispatching/validation/Validator.html)
- [TypeAdapter](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/dispatching/adapter/TypeAdapter.html)
- [PermissionsProvider](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/permissions/PermissionsProvider.html)
- [GuildScopeProvider](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/scope/GuildScopeProvider.html)
- [ErrorMessageFactory](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/embeds/error/ErrorMessageFactory.html)
- [Descriptor](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/definitions/description/Descriptor.html)

To make these implementations discoverable please annotate the involved classes with [`@Implementation`](jda.commands.guice/com/github/kaktushose/jda/commands/guice/Implementation.html).
If you're implementing a
[TypeAdapter](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/dispatching/adapter/TypeAdapter.html),
[Middleware](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/dispatching/middleware/Middleware.html) or
[Validator](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/dispatching/validation/Validator.html)
you have to provide additionally information in [`@Implementation`](jda.commands.guice/com/github/kaktushose/jda/commands/guice/Implementation.html). 
Please visit the docs of this class to gain more information.

#### Example

 ```java
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.guice.Implementation;

@Implementation(priority = Priority.NORMAL)
public class CustomMiddleware implements Middleware {
    private static final Logger log = LoggerFactory.getLogger(FirstMiddleware.class);

    @Override
    public void accept(InvocationContext<?> context) {
        log.info("run custom middleware");
    }
}
 ```


