package com.github.kaktushose.jda.commands.annotations;

import com.github.kaktushose.jda.commands.annotations.constraints.Constraint;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;

import java.lang.annotation.*;

/**
 * Indicates that the annotated class is a custom implementation that should replace the default implementation.
 *
 * @author Kaktushose
 * @version 2.2.0
 * @see ImplementationRegistry ImplementationRegistry
 * @since 2.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Implementation {

    /**
     * Gets the {@link Priority} to register the
     * {@link com.github.kaktushose.jda.commands.dispatching.middleware.Middleware Middleware} with. If this
     * implementation is not a subtype
     * of {@link com.github.kaktushose.jda.commands.dispatching.middleware.Middleware Middleware}, this field can be
     * ignored.
     *
     * @return the {@link Priority}
     */
    Priority priority() default Priority.NORMAL;

    /**
     * Gets the annotation the {@link com.github.kaktushose.jda.commands.dispatching.validation.Validator Validator}
     * should be mapped to. If the component is not a subtype of
     * {@link com.github.kaktushose.jda.commands.dispatching.validation.Validator Validator}, this field can be ignored.
     *
     * @return the annotation the {@link com.github.kaktushose.jda.commands.dispatching.validation.Validator Validator}
     * should be mapped to
     */
    Class<? extends Annotation> annotation() default Constraint.class;

}
