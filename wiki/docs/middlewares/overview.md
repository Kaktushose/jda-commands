# Middlewares
Middlewares run just before an interaction event gets dispatched. They are used to perform additional checks or add more 
info the [`InvocationContext`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/dispatching/context/InvocationContext.html).
Middlewares are intended to provide a flexible system for extending the execution chain.

They are executed based on their [`Priority`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/dispatching/middleware/Priority.html)
in the following order:

1. `PERMISSIONS`: Middlewares with this priority will **always** be executed first
2. `HIGH`: Highest priority for custom implementations, will be executed right after internal middlewares
3. `NORMAL`: Default priority
4. `LOW`: Lowest priority, will be executed at the end

If one middleware fails, the entire interaction execution gets immediately aborted and no more middlewares will be executed. 

## Default Middlewares
JDA-Commands uses its own Middleware API internally to implement some features. All these features can either be 
*extended* or *replaced* by the user. You can either register your own implementations at the respective builder method
or use the [`@Implementation`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.extension.guice/com/github/kaktushose/jda/commands/guice/Implementation.html) annotation.

!!! note
    Using the [`@Implementation`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.extension.guice/com/github/kaktushose/jda/commands/guice/Implementation.html)
    annotation requires the guice integration (shipped by default). You can read more about it [here](../di.md).

Middlewares provided by JDA-Commands include:

- [Type Adapters](./typeadapter.md)
- [Parameter Validation](./validator.md)
- [Permissions System](./permissions.md)
- [Command Cooldown](./cooldown.md)

## Writing own Middlewares

You can write your own middlewares by implementing the [`Middleware`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/dispatching/middleware/Middleware.html) interface.
You can cancel an execution by calling [`context.cancel(message)`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/dispatching/context/InvocationContext.html#cancel(net.dv8tion.jda.api.utils.messages.MessageCreateData)).


!!! example
    ```java
    public class LoggingMiddleware implements Middleware {
        
        public void accept(InvocationContext<?> context) {
            Logger.log(context.event());
        }

    }
    ```

Then, either register your Middleware at the [builder](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/JDACBuilder.html#middleware(com.github.kaktushose.jda.commands.dispatching.middleware.Priority,com.github.kaktushose.jda.commands.dispatching.middleware.Middleware)):
```java
JDACommands.builder(jda, Main.class)
    .middleware(Priority.NORMAL, new LoggingMiddleware());
    .start();
```

or use the [`@Implementation`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.extension.guice/com/github/kaktushose/jda/commands/guice/Implementation.html)
annotation:
```java
@Implementation.Middleware(priority = Priority.NORMAL)
public class LoggingMiddleware implements Middleware {...}
```


### Run only for certain interaction controllers
If you want your Middleware to only run for certain interaction controllers, just implement [Middleware#runFor()](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/dispatching/middleware/Middleware.html#runFor())
returning the classes of the interaction controllers for which the middleware should run.

!!! example (run only for HelloController.java)
    ```java
    @Middleware(priority = Priority.NORMAL)
    public class CustomMiddleware implements Middleware {
        private static final Logger log = LoggerFactory.getLogger(FirstMiddleware.class);

        @Override
        public void accept(InvocationContext<?> context) {
            log.info("run custom middleware");
        }
 
        @Override
        public Collection<Class<?>> runFor() {
            return List.of(HelloController.class);
        }
    }
    ```