package io.github.kaktushose.jdac.definitions.description;

import java.lang.annotation.Annotation;
import java.util.*;

/// A [Description] that describes a class.
///
/// @param clazz              the [Class] this [Description] describes.
/// @param name               the full name including packages of the class
/// @param packageDescription the [PackageDescription] representing the package returned by [Class#getPackage()]
/// @param annotations        a [Collection] of all [Annotation]s this class is annotated with
/// @param methods            a [Collection] of all the public [`methods`][MethodDescription] of this class
public record ClassDescription(
        Class<?> clazz,
        String name,
        PackageDescription packageDescription,
        Collection<AnnotationDescription<?>> annotations,
        Collection<MethodDescription> methods
) implements Description {
    public ClassDescription(Class<?> clazz, String name, PackageDescription packageDescription, Collection<AnnotationDescription<?>> annotations, Collection<MethodDescription> methods) {
        this.clazz = clazz;
        this.name = name;
        this.packageDescription = packageDescription;
        this.annotations = Collections.unmodifiableCollection(annotations);
        this.methods = Collections.unmodifiableCollection(methods);
    }

    /// Gets a method that matches the given name and parameter types if found.
    ///
    /// @param name the method name
    /// @param parameters the method parameters, as stored by [ParameterDescription#type()]
    ///
    /// @return the found [MethodDescription] or [Optional#empty()]
    public Optional<MethodDescription> findMethod(String name, List<Class<?>> parameters) {
        return methods
                .stream()
                .filter(desc -> desc.name().equals(name) && desc.parameters()
                        .stream()
                        .map(ParameterDescription::type)
                        .toList().equals(parameters))
                .findFirst();
    }

    /// Gets a method that matches the given name and parameter types if found.
    ///
    /// @param name the method name
    /// @param parameters the method parameters, as stored by [ParameterDescription#type()]
    ///
    /// @return the found [MethodDescription] or [Optional#empty()]
    public Optional<MethodDescription> findMethod(String name, Class<?>... parameters) {
        return findMethod(name, Arrays.asList(parameters));
    }

    /// Gets a method that matches the given name and parameter types.
    /// Throws if no matching method was found.
    ///
    /// @param name the method name
    /// @param parameters the method parameters, as stored by [ParameterDescription#type()]
    ///
    /// @return the found [MethodDescription]
    ///
    /// @throws NoSuchElementException if no element was found
    /// @see Optional#orElseThrow()
    public MethodDescription method(String name, List<Class<?>> parameters) {
        return findMethod(name, parameters).orElseThrow();
    }

    /// Gets a method that matches the given name and parameter types.
    /// Throws if no matching method was found.
    ///
    /// @param name the method name
    /// @param parameters the method parameters, as stored by [ParameterDescription#type()]
    ///
    /// @return the found [MethodDescription]
    ///
    /// @throws NoSuchElementException if no element was found
    /// @see Optional#orElseThrow()
    public MethodDescription method(String name, Class<?>... parameters) {
        return method(name, Arrays.asList(parameters));
    }

    @Override
    public String toString() {
        return "ClassDescription(%s)".formatted(name);
    }
}
