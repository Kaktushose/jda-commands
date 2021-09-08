package com.github.kaktushose.jda.commands.rewrite.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link CommandController} classes or {@link Command} methods annotated with Permission will
 * require the user to have the given permissions in order to execute the command.
 *
 * <p>The permission levels are described by a String, e.g. <em>user.ban</em>, and will be compared to the permissions a user
 * has, before a command gets invoked. Permission levels are added to users through
 * the {@link com.github.kaktushose.jda.commands.entities.CommandSettings}.
 *
 * <p>If a class is annotated with Permission all methods or respectively all commands will require the given permission level.
 *
 * @author Kaktushose
 * @version 1.0.0
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
    String[] value();
}
