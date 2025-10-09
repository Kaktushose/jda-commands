# Writing an Extension

## Entrypoint

The entrypoint of the Extension API is the so called
<com.github.kaktushose.jda.commands.extension.Extension>
interface, which your extensions _"entry class"_ must implement:

```java
public class MyExtension implements Extension<?> {}
```

### `Extension.Data`

Furthermore, each entry class must override the <Extension#init(T)> method,
which will be called when JDA-Commands loads the extension. It can be used to configure extension specific options with help of
an own implementation of
<Extension.Data>.

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
<ExtensionProvidable>,
that is:

- `Descriptor`
- `InteractionControllerInstantiator`
- `ErrorMessageFactory`
- `Implementation.MiddlewareContainer` (wrapper type for `Middleware`)
- `Implementation.TypeAdapterContainer` (wrapper type for `TypeAdapter`)
- `Implementation.ValidatorContainer` (wrapper type for `Validator`)
- `PermissionsProvider`
- `GuildScopeProvider`

To provide custom implementations we have to implement the <Extension#providedImplementations()> method.
This method returns a collection of all implementations that an extension provides, wrapped in an instance of
<com.github.kaktushose.jda.commands.extension.Implementation>

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

### Identity Equality
!!! warning
    `providedImplementations()` and the supplier of `Implementation` will be called
    multiple times during JDA-Commands startup!

Thus, `providedImplementations()` must always return the same enumeration of `Implementation`s. If the identity 
equality is important, the supplier of `Implementation` must also always return the same instances.

JDA-Commands doesn't rely on identity equality, but you might have fields that store information in that class.
It should, of course, be the same instance then each time. 

`MyExtension` could look something like this in that case:

```java
public class MyExtension implements Extension<MyExtensionData> {

    private MyCustomDescriptor descriptor;
    
    @Override
    public void init(MyExtensionData data) {
        if (data != null) {
            doSomeConfig(data.someOption());
            descriptor = new MyCustomDescriptor(this);
        }
    }

    @Override
    public @NotNull Collection<Implementation<?>> providedImplementations() {
        return List.of(new Implementation.single(
                Descriptor.class,
                _ -> descriptor)
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
The <com.github.kaktushose.jda.commands.extension.Implementation>
class has 2 main purposes: State which class the custom implementation is for and providing instance(s) of those custom implementations.

To provide a custom implementation you have to create an instance of <com.github.kaktushose.jda.commands.extension.Implementation> and provide

1. the `type` of this Extension [(that is a class/interface extending ExtensionProvidable](#providing-implementations))
2. a `supplier` in form of `java.util.fuction.Function` that takes
  <JDACBuilderData>
  as an argument and returns a list of instances of custom implementations for the specific type


```java title="Example for ClassFinder"
new Implemenation(
        ClassFinder.class,
        builderData -> List.of(new CustomClassFinderOne(), new CustomClassFinderSecond(builderData.descriptor()))
)
```

It's also important that only the following types support multiple instances:

- `ClassFinder`
- `Implementation.MiddlewareContainer` (wrapper type for Middleware)
- `Implementation.TypeAdapterContainer` (wrapper type for TypeAdapter)
- `Implementation.ValidatorContainer` (wrapper type for Validator)

For all other types
<Implementation#single(Class,Function)>
should be used.

The provided instance of `JDACBuilderData` only supports read access to the builder, which can be used to obtain any other
part of the framework as a dependency. It's important to have in mind, that calls to this object will check all registered
extensions for the needed implementation, thus cycling dependencies will result in an exception.

!!! tip
    If the list returned by **supplier** is empty, this implementation will be treated as non-existent, 
    which is useful for dynamic registration of custom implementation.

## Registration
Custom extensions are found with help of Javas [ServiceLoader API][[ServiceLoader]].

To register the above `MyExtension` we have to create a file in our `resources\META-INF` directory called
`com.github.kaktushose.jda.commands.extension.Extension`.

```
src
└── main
    └── resources
        └── META-INF
            └── com.github.kaktushose.jda.commands.extension.Extension
```

The full class name of our class `MyExtension` (e.g. `my.package.MyExtension`) must be the content of this file.

!!! example
    ```text title="com.github.kaktushose.jda.commands.extension.Extension"
    my.package.MyExtension
    ```

The extension can now be found and loaded by JDA-Commands.