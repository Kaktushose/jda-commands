package com.github.kaktushose.jda.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Methods annotated with Produces will be used to get the instance of a dependency.
///
/// The instances provided by producer methods are used to inject values to the fields inside a
/// [Interaction][com.github.kaktushose.jda.commands.annotations.interactions.Interaction]
/// that are annotated with [Inject]. The access modifier of a producer method must be
/// public.
///
/// Classes containing producer methods will be found automatically on startup. They can also be registered via
/// [DependencyInjector.registerProvider(Object)][#registerProvider(Object)]
///
/// Please note that this is only a very basic implementation of dependency injection and can only be used inside
/// interaction controller classes or custom [implementations][Implementation]. Furthermore, each type can only
/// have one producer. In other words you cannot register different instances of the same dependency.
///
/// @see Inject
/// @see com.github.kaktushose.jda.commands.dependency.DependencyInjector DependencyInjector
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Produces {

    /// Whether jda-commands should ignore this method at indexing during startup. Useful if you wish to register your
    /// dependency providers manually by calling
    /// [DependencyInjector#registerProvider(Object)][#registerProvider(Object)]
    ///
    /// @return Whether jda-commands should ignore this method, default `true`
    boolean skipIndexing() default false;

}
