package com.github.kaktushose.jda.commands.annotations.interactions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link com.github.kaktushose.jda.commands.annotations.interactions.Interaction Interaction} classes or
 * {@link SlashCommand} methods annotated with Permission will
 * require the user to have the given permissions in order to execute the command.
 *
 * <p>The default implementation of this framework can only handle discord permissions.
 * However, the {@link com.github.kaktushose.jda.commands.permissions.PermissionsProvider PermissionsProvider} interface
 * allows custom implementations.
 *
 * <p>If a class is annotated with Permission all methods or respectively all commands will require the given
 * permission level.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see com.github.kaktushose.jda.commands.permissions.PermissionsProvider PermissionsProvider
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {

    /**
     * Returns a String array of all required permissions.
     *
     * @return a String array of all required permissions.
     */
    String[] value();
}
