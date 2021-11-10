package com.github.kaktushose.jda.commands.annotations;

import com.github.kaktushose.jda.commands.dispatching.CommandEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated with Command will be registered as a command at startup.
 *
 * <p>Therefore the method must be declared inside a class that is annotated with {@link CommandController}.
 * Furthermore, the method signature has to meet the following conditions:
 * <ul>
 * <li>First parameter must be of type {@link CommandEvent}</li>
 * <li>Remaining parameter types must be registered at the {@link com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry} or be a String array</li>
 * <li>Parameter constraints must be valid</li>
 * </ul>
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see CommandController
 * @see com.github.kaktushose.jda.commands.annotations.constraints.Constraint
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String[] value() default "";

    boolean isSuper() default false;

    boolean isDM() default true;

    String name() default "N/A";

    String desc() default "N/A";

    String usage() default "N/A";

    String category() default "Other";

    boolean isActive() default true;
}
