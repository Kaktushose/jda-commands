package com.github.kaktushose.jda.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated with Produces will be used to get the instance of a dependency.
 *
 * <p>The instances provided by producer methods are used to inject values to the fields inside a {@link CommandController} that are annotated with
 * {@link Inject}. All producer methods must be declared inside a class that implements {@link com.github.kaktushose.jda.commands.api.Provider}.
 * Also the access modifier of a producer method must be public.
 *
 * @author Kaktushose
 * @version 1.0.0
 * @see Inject
 * @see com.github.kaktushose.jda.commands.api.Provider
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Produces {
}
