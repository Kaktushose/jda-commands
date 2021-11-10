package com.github.kaktushose.jda.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated with Produces will be used to get the instance of a dependency.
 *
 * <p>The instances provided by producer methods are used to inject values to the fields inside a {@link CommandController}
 * that are annotated with {@link Inject}. The access modifier of a producer method must be public.
 * Classes containing producer methods will be found automatically on startup.
 * The can also be registered via {@link com.github.kaktushose.jda.commands.dependency.DependencyInjector#registerProvider(Object)}
 *
 * <p>Please note that this is only a very basic implementation of dependency injection and should only be used inside actual command classes.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see Inject
 * @see com.github.kaktushose.jda.commands.dependency.DependencyInjector
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Produces {
}
