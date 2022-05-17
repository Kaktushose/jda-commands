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
 * <li>Remaining parameter types must be registered at the
 * {@link com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry TypeAdapterRegistry} or be a
 * String array</li>
 * <li>Parameter constraints must be valid</li>
 * </ul>
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see CommandController
 * @see com.github.kaktushose.jda.commands.annotations.constraints.Constraint Constraint
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    /**
     * Returns a String array of all labels.
     *
     * @return a String array of all labels
     */
    String[] value() default "";

    /**
     * Returns whether this command is a super command. A super command will be listed separately in the default help
     * embed and will have all non-super commands inside the same controller as a sub command.
     *
     * @return {@code true} if this is a super command
     */
    boolean isSuper() default false;

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
     * Returns whether this command is available to everyone by default. If this is disabled, you need to
     * explicitly whitelist users and roles per guild via
     * {@link com.github.kaktushose.jda.commands.permissions.PermissionsProvider PermissionsProvider}.
     *
     * @return {@code true} if this command is available to everyone by default
     */
    boolean defaultEnable() default true;

    /**
     * Returns whether this command should send ephemeral replies by default. This only affects slash commands.
     *
     * @return {@code true} if this command is available to everyone by default
     */
    boolean ephemeral() default false;

}
