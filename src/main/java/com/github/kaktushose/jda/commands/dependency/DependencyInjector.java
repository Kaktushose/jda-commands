package com.github.kaktushose.jda.commands.dependency;

import com.github.kaktushose.jda.commands.annotations.Produces;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;

/// Central registry for dependencies and producing methods. Registered dependencies will be injected with the
/// corresponding values if present, else null.
///
/// @see Produces
/// @see com.github.kaktushose.jda.commands.annotations.Inject Inject
public interface DependencyInjector {

    /// Scans the whole classpath for methods annotated with [Produces]. If found, creates a new instance of
    /// the declaring class and will call the method to retrieve the object and register it as a dependency. If the method
    /// is declared as static, no instance will be created. Use [#registerProvider(Object)] if no new instance of
    /// the declaring class should be created.
    ///
    /// @param packages package(s) to exclusively scan
    /// @param clazz    a class of the classpath to scan
    void index(@NotNull Class<?> clazz, @NotNull String... packages);

    /// Takes an instance of a class and scans it for methods annotated with [Produces]. If found, will call
    /// the method to retrieve the object and register it as a dependency.
    ///
    /// @param provider instance of the class to scan
    void registerProvider(@NotNull Object provider);

    /// Registers fields inside a class as dependencies.
    ///
    /// @param clazz  the declaring class
    /// @param fields the dependencies to register
    void registerDependencies(@NotNull Class<?> clazz, @NotNull List<Field> fields);

    /// Injects all registered dependencies with the corresponding value. If no value is present `null` gets injected.
    void inject(Object instance);
}

