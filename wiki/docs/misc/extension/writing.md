# Writing an Extension

## Entrypoint

The entrypoint of the Extension API is the so called
<io.github.kaktushose.jdac.extension.Extension>
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
Currently, extensions support to provide custom [PropertyProviders](../property.md#propertyprovider) of properties with
category <Property.Category#LOADABLE>. You can take a look at <Property> to know what properties can be provided by extensions. 

To provide custom <PropertyProviders> your have to implement the <Extension#properties()> method.
This method returns a collection of all <PropertyProviders> that an extension provides. Take a look [here](../property.md#propertyprovider)
to know how to use them.

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
        return List.of(new PropertyProvider(
                Property.CLASS_FINDER,
                200, // pick an appropriated priority
                Foo.class,
                ctx -> List.of(new CustomClassFinder(ctx.get(Priority.PACKAGES)))
        ));
    }

    @Override
    public @NotNull Class<MyExtensionData> dataType() {
        return MyExtensionData.class;
    }
}

public record MyExtensionData(String someOption) implements Extension.Data {}
```

## Registration
Custom extensions are found with help of Javas [ServiceLoader API][[ServiceLoader]].

To register the above `MyExtension` we have to create a file in our `resources\META-INF` directory called
`io.github.kaktushose.jdac.extension.Extension`.

```
src
└── main
    └── resources
        └── META-INF
            └── io.github.kaktushose.jdac.extension.Extension
```

The full class name of our class `MyExtension` (e.g. `my.package.MyExtension`) must be the content of this file.

!!! example
    ```text title="io.github.kaktushose.jdac.extension.Extension"
    my.package.MyExtension
    ```

The extension can now be found and loaded by JDA-Commands.