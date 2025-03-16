# Writing an extension
The main component of the extension api is the so called
[`Extension`](https://kaktushose.github.io/jda-commands/javadocs/latest/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/extension/Extension.html)
interface, which your extension's "entry class" must implement:

```java
public class MyExtension implements Extension<?> {}
```

Furthermore, each entry class must override `Extension`'s `init(T data)` method,
which will be called when loading the extension. It can be used to configure extension specific options with help of
an own implementation of
[`Extension.Data`](https://kaktushose.github.io/jda-commands/javadocs/development/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/extension/Extension.Data.html)

```java
public class MyExtension implements Extension<Void> {
    
    @Override
    public void init(Void data) {
        // doing nothing if not needed
    }
}

public class MyExtension implements Extension<MyExtensionData> {

    @Override
    public void init(MyExtensionData data) { //(1)!
        if (data != null) {
            doSomeConfig(data.someOption());
        }
    }

    @Override
    public @NotNull Class<MyExtensionData> dataType() {
        return MyExtensionData.class;
    }
}

public record MyExtensionData(String someOption) implements Extension.Data {}
```

1. If no instance of `MyExtensionData` is passed by the user, this argument will be set `null`.

## Providing Implementations
Currently, extensions support to provide custom implementations of any class extending
[`ExtensionProvideable`](https://kaktushose.github.io/jda-commands/javadocs/development/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/extension/Implementation.ExtensionProvidable.html)
that is:

- ClassFinder
- Descriptor
- InteractionControllerInstantiator
- ErrorMessageFactory
- Implementation.MiddlewareContainer (wrapper type for Middleware)
- Implementation.TypeAdapterContainer (wrapper type for TypeAdapter)
- Implementation.ValidatorContainer (wrapper type for Validator)
- PermissionsProvider
- GuildScopeProvider

To provide custom implementations we have to implement `Extensions`'s `providedImplementations()` method.
This method returns a collection of all implementations wrapped in an instance of
[`Implementation`](https://kaktushose.github.io/jda-commands/javadocs/latest/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/extension/Implementation.html)
that an extension provides.

```java
public class MyExtension implements Extension<MyExtensionData> {

    @Override
    public void init(MyExtensionData data) {
        if (data != null) {
            doSomeConfig(data.someOption());
        }
    }

    @Override
    public @NotNull Collection<Implementation<?>> providedImplementations() {
        return List.of(new Implementation.single(
                Descriptor.class,
                _ -> new MyCustomDescriptor(this))
        );
    }

    @Override
    public @NotNull Class<MyExtensionData> dataType() {
        return MyExtensionData.class;
    }
}

public record MyExtensionData(String someOption) implements Extension.Data {}
```

### The `Implementation` class
The [`Implementation`](https://kaktushose.github.io/jda-commands/javadocs/latest/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/extension/Implementation.html)
class has 2 main purposes: State which class the implementation is for and providing instance(s) of custom implementations.

To provide a custom implementation you have to create an instance of `Implementation`, with

- the **type** [(that is a class/interface extending ExtensionProvidable)](#providing-implementations) of this Extension
- a **supplier** in form of `java.util.fuction.Function` that takes
  [`JDACBuilderData`](https://kaktushose.github.io/jda-commands/javadocs/latest/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/extension/JDACBuilderData.html)
  as an argument returns a list of instances of custom implementations for the specific type

Example for ClassFinder:
```java
new Implemenation(
        ClassFinder.class,
        builderData -> List.of(new CustomClassFinderOne(), new CustomClassFinderSecond(builderData.descriptor()))
)
```

It's also important, that only:

- ClassFinder
- Implementation.MiddlewareContainer (wrapper type for Middleware)
- Implementation.TypeAdapterContainer (wrapper type for TypeAdapter)
- Implementation.ValidatorContainer (wrapper type for Validator)

support multiple instances. For all other types
[`single(Class<T>,Function<JDACBuilderData,T> supplier)`](https://kaktushose.github.io/jda-commands/javadocs/latest/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/extension/Implementation.html#single(java.lang.Class,java.util.function.Function))
should be used.

The provided instance of `JDACBuilderData` only supports read access to the builder, which can be used to obtain any other
part of the framework as a dependency. It's important to have in mind, that calls to this object will check all registered
extensions for the needed implementation, thus cycling dependencies will result in an exception.

!!! note 
    If the list returned by **supplier** is empty, this implementation will be treated as non-existent, 
    which is useful for dynamic registration of custom implementation.

## Registration
Custom extensions are found with help of java's [ServiceLoader api](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html).

To register the above `MyExtension` we have to create a file in our "resources" directory in the subdirectory "META-INF" called
"com.github.kaktushose.jda.commands.extension.Extension" with the full class name of our class "MyExtension" as the content:
"my.package.MyClass":

```text title="com.github.kaktushose.jda.commands.extension.Extension"
my.package.MyClass
```

The extension can now be found by jda-commands and loaded.