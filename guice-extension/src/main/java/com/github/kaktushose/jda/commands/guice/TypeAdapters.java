package com.github.kaktushose.jda.commands.guice;

import com.github.kaktushose.jda.commands.definitions.description.ClassFinder;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import jakarta.inject.Scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Indicates that the annotated class is a custom implementation that should replace the default implementation.
///
/// A class annotated with [TypeAdapters] will be automatically searched for with help of the [ClassFinder]s
/// and instantiated as a [TypeAdapter] by guice.
///
/// ### Example
/// ```java
/// @TypeAdapters(clazz = CustomType.class)
/// public class CustomTypeAdapter implements TypeAdapter<CustomType> {
///
///     public Optional<CustomType> apply(String raw, GenericInteractionCreateEvent event) {
///         return Optional.of(new CustomType(raw, event));
///     }///
/// }
/// ```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Scope
public @interface TypeAdapters {

    /// Gets the [Class] to register a [TypeAdapter] with.
    ///
    /// @return the class the [TypeAdapter] should be mapped to
    Class<?> clazz() default Object.class;

}
