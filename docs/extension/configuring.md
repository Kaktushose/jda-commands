# Configuring extensions

## Passing instances of `Extension.Data`
If we want to configure an extension, we can pass an instance of the extension specific implementation of `Extension.Data`
to the JDA-Commands builder. In case of our `MyExtension` example, that would be:

```java
 JDACommands.builder(jda, Main.class)
         .extensionData(new MyExtensionData("someValue"))
         .start();
```

## Filtering found Extensions
Filtering extensions is crucial for resolving cycling dependencies.
To filter which extensions we want to include in our application, we can utilize the
[`JDACBuilder#filterExtensions(JDACBuilder.FilterStrategy, String...)`](https://kaktushose.github.io/jda-commands/javadocs/development/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/JDACBuilder.html#filterExtensions(com.github.kaktushose.jda.commands.JDACBuilder.FilterStrategy,java.lang.String...))
method.

1. parameter: [`JDACBuilder.FilterStrategy`](https://kaktushose.github.io/jda-commands/javadocs/latest/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/JDACBuilder.FilterStrategy.html),
   which will either exclude (`FilterStrategy.EXCLUDE`) or include (`FilterStrategy.INCLUDE`) the passed extensions.
2. parameter: `String...` is an enumeration of the full class names, which should be either included or excluded.
   The strings will be matched to the full class names of the classes extending `Extension` using `String#startWith`, thus
   specifying package names is possible.

To for example exclude the default Guice Extension, we could call:

```java
    JDACommands.builder(jda, Main.class)
            .filterExtensions(FilterStrategy.EXCLUDE, "com.github.kaktushose.jda.commands.guice")
            .start();
```