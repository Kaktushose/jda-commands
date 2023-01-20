package com.github.kaktushose.jda.commands.annotations;

import com.github.kaktushose.jda.commands.dispatching.CommandEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated with SlashCommand will be registered as a command at startup.
 *
 * <p>Therefore the method must be declared inside a class that is annotated with
 * {@link com.github.kaktushose.jda.commands.annotations.interactions.Interaction}.
 * Furthermore, the method signature has to meet the following conditions:
 * <ul>
 * <li>First parameter must be of type {@link CommandEvent}</li>
 * <li>Remaining parameter types must be registered at the
 * {@link com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry TypeAdapterRegistry} or be a
 * String array</li>
 * <li>Parameter constraints must be valid</li>
 * </ul>
 *
 * @author Kaktushose
 * @version 4.0.0
 * @see com.github.kaktushose.jda.commands.annotations.interactions.Interaction Interaction
 * @see com.github.kaktushose.jda.commands.annotations.constraints.Constraint Constraint
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SlashCommand {

    /**
     * Retruns the label of the command.
     *
     * @return the label of the command
     */
    String value() default "";

    /**
     * Returns whether this command can be executed in direct messages.
     *
     * @return {@code true} if this command can be executed in direct messages
     */
    boolean isDM() default true;

    /**
     * Returns the name of the command. This is <em>not</em> the command label.
     *
     * @return the name of the command
     */
    String name() default "N/A";

    /**
     * Returns the description of the command.
     *
     * @return the description of the command
     */
    String desc() default "N/A";

    /**
     * Returns the usage of the command.
     *
     * @return the usage of the command
     */
    String usage() default "N/A";

    /**
     * Returns the category of the command.
     *
     * @return the category of the command
     */
    String category() default "Other";

    /**
     * Returns whether this command is active and thus can be executed or not.
     *
     * @return {@code true} if this command is active
     */
    boolean isActive() default true;

    /**
     * Returns whether this command should send ephemeral replies by default. This only affects slash commands.
     *
     * @return {@code true} if this command is available to everyone by default
     */
    boolean ephemeral() default false;

}
