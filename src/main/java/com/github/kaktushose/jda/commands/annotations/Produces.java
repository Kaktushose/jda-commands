package com.github.kaktushose.jda.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated with Produces will be used to get the instance of a dependency.
 *
 * <p>The instances provided by producer methods are used to inject values to the fields inside a
 * {@link com.github.kaktushose.jda.commands.annotations.interactions.Interaction Interaction}
 * that are annotated with {@link Inject}. The access modifier of a producer method must be
 * public.
 *
 * <p>Classes containing producer methods will be found automatically on startup. They can also be registered via
 * {@link com.github.kaktushose.jda.commands.dependency.DependencyInjector#registerProvider(Object)
 * DependencyInjector.registerProvider(Object)}
 *
 * <p>Please note that this is only a very basic implementation of dependency injection and can only be used inside
 * interaction controller classes or custom {@link Implementation implementations}. Furthermore, each type can only
 * have one producer. In other words you cannot register different instances of the same dependency.
 *
 * @see Inject
 * @see com.github.kaktushose.jda.commands.dependency.DependencyInjector DependencyInjector
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Produces {

}
