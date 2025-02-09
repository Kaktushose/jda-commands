package com.github.kaktushose.jda.commands.definitions.description;

import com.github.kaktushose.jda.commands.definitions.description.reflective.ReflectiveClassFinder;
import com.github.kaktushose.jda.commands.extension.Implementation;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.SequencedCollection;

/// [ClassFinder]s search for classes annotated with a specific annotation
public non-sealed interface ClassFinder extends Implementation.ExtensionProvidable {

    /// This method searches for classes annotated with the given annotation.
    ///
    /// @param annotationClass the class of the annotation
    /// @return the found classes
    @NotNull
    SequencedCollection<Class<?>> search(@NotNull Class<? extends Annotation> annotationClass);

    /// This method searches for classes annotated with the given annotation, which have the given super type.
    ///
    /// @param annotationClass the class of the annotation
    /// @param superType the [Class], which is a supertype of the found classes
    /// @return the found classes
    @SuppressWarnings("unchecked")
    @NotNull
    default <T> SequencedCollection<Class<T>> search(@NotNull Class<? extends Annotation> annotationClass, @NotNull Class<T> superType) {
        return search(annotationClass).stream()
                .filter(superType::isAssignableFrom)
                .map(aClass -> (Class<T>) aClass)
                .toList();
    }

    /// This provides a reflections based implementation of [ClassFinder]
    ///
    /// @param baseClass The [Class] providing the used [ClassLoader]
    /// @param packages a list of packages that should be scanned
    @NotNull
    static ClassFinder reflective(@NotNull Class<?> baseClass, @NotNull String... packages) {
        return new ReflectiveClassFinder(baseClass, packages);
    }
}
