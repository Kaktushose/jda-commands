package com.github.kaktushose.jda.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Classes annotated with CommandController will be scanned at startup.
 *
 * <p>All methods annotated with {@link Command} will be registered as a command.
 * All fields annotated with {@link Inject} will be assigned a value. If any errors occur only the affected method or field
 * will be skipped.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see Command
 * @see Inject
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandController {

    /**
     * Returns a String array of all labels.
     *
     * @return a String array of all labels
     */
    String[] value() default "";

    /**
     * Returns the category of the command.
     *
     * @return the category of the command
     */
    String category() default "Other";

    /**
     * Returns whether this command is active and thus can be executed or not
     *
     * @return {@code true} if this command is active
     */
    boolean isActive() default true;

    /**
     * Returns Whether this command is available to everyone by default. If this is disabled, you need to
     * explicitly whitelist users and roles per guild via
     * {@link com.github.kaktushose.jda.commands.permissions.PermissionsProvider PermissionsProvider}. This will
     * override command level values.
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
