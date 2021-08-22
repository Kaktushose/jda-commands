package com.github.kaktushose.jda.commands.annotations;

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
 * <li>public access modifier and no return type</li>
 * <li>First parameter must be of type {@link com.github.kaktushose.jda.commands.entities.CommandEvent}</li>
 * <li>Remaining parameters must either be parsable by the {@link com.github.kaktushose.jda.commands.api.ArgumentParser} or be a String array</li>
 * <li>Parameter options defined by {@link Optional} and by {@link Concat} must be valid</li>
 * </ul>
 *
 * @author Kaktushose
 * @version 1.0.0
 * @see CommandController
 * @see com.github.kaktushose.jda.commands.entities.CommandEvent
 * @see com.github.kaktushose.jda.commands.api.ArgumentParser
 * @see Concat
 * @see Optional
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
