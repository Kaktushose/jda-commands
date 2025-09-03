# Avoiding Reflections
JDA-Commands uses `java.lang.reflect` in two places:

- [`ClassFinder`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/definitions/description/ClassFinder.html)
- [`Descriptor`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/definitions/description/Descriptor.html)

If you want to completely avoid `java.lang.reflect` you have to provide your own implementations.

## ClassFinder
`ClassFinders` are used to provide instances of `Class` that will be scanned for interactions or custom implementations. 
You can register at the JDA-Commands Builder.

```java
JDACommands.builder(jda, Main.class)
    .classFinders(new CustomClassFinder());
    .start();
```

Use [`ClassFinder#explicit(...)`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/definitions/description/ClassFinder.html#explicit(java.lang.Class...))
if you want to explicitly add a `Class`. 

!!! warning
    Calling `classFinders(...)` on the builder will override existing class finders. If you want to keep the default 
    reflective class finder, you have to add it again via [`ClassFinder#reflective(...)`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/definitions/description/ClassFinder.html#reflective(java.lang.Class,java.lang.String...)). 

```java
JDACommands.builder(jda, Main.class)
    .classFinders(ClassFinder.explicit(ForeignClass.class), ClassFinder.reflective(Main.class, "com.package"));
    .start();
```

## Descriptor
A `Descriptor` takes a `Class` as input and transforms it into a [`ClassDescription`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/definitions/description/ClassDescription.html).
Descriptors can also be registered using the [`@Implementation`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.extension.guice/com/github/kaktushose/jda/commands/guice/Implementation.html)
annotation. Alternatively, register them at the JDA-Commands Builder.

```java
JDACommands.builder(jda, Main.class)
    .descriptor(new CustomDescriptor());
    .start();
```