# Avoiding Reflections
JDA-Commands uses `java.lang.reflect` in two places:

- <ClassFinder>
- <io.github.kaktushose.jdac.definitions.description.Descriptor>
- <EmojiSource>

If you want to completely avoid `java.lang.reflect` you have to provide your own implementations.

## ClassFinder
`ClassFinders` are used to provide instances of `Class` that will be scanned for interactions or custom implementations. 
You can register at the JDA-Commands Builder.

```java
JDACommands.builder(jda, Main.class)
    .classFinders(new CustomClassFinder())
    .start();
```

Use <ClassFinder#explicit(...)>
if you want to explicitly add a `Class`. 

!!! warning
    Calling `classFinders(Class...)` on the builder will override existing class finders. If you want to keep the default 
    reflective class finder, you have to add it again via <ClassFinder#reflective(String...)>. 

```java
JDACommands.builder(jda, Main.class)
    .classFinders(ClassFinder.explicit(ForeignClass.class), ClassFinder.reflective(Main.class, "com.package"))
    .start();
```

## Descriptor
A `Descriptor` takes a `Class` as input and transforms it into a <ClassDescription>.
Descriptors can also be registered using the <io.github.kaktushose.jdac.guice.Implementation>
annotation. Alternatively, register them at the JDA-Commands Builder.

```java
JDACommands.builder(jda, Main.class)
    .descriptor(new CustomDescriptor());
    .start();
```


## EmojiSource
[EmojiSources](../message/emojis.md#automatic-application-emojis-registration) are used to load application emojis that should be registered automatically upon startup for you. 
They're similar to [ClassFinders](#classfinder).

You can register them at the JDA-Commands Builder or via the <io.github.kaktushose.jdac.guice.Implementation> annotation.

```java
JDACommands.builder(jda, Main.class)
    .emojiSources(new CustomEmojiSource())
    .start();
```

!!! warning
    Calling `emojiSources(...)` on the builder will override existing emoji sources. If you want to keep the default
    reflective emoji source, you have to add it again via <EmojiSource#reflective(String...)>. 