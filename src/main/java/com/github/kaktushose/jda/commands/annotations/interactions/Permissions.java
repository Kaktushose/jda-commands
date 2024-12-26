package com.github.kaktushose.jda.commands.annotations.interactions;

import com.github.kaktushose.jda.commands.permissions.PermissionsProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// [Interaction] classes or
/// interaction methods (commands, components or modals) annotated with Permission will
/// require the user to have the given permissions in order to execute the command.
///
/// The default implementation of this framework can only handle discord permissions.
/// However, the [PermissionsProvider] interface allows custom implementations.
///
/// If a class is annotated with Permission all methods or respectively all interactions will require the given
/// permission level.
///
/// @see PermissionsProvider PermissionsProvider
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Permissions {

    /// Returns a String array of all required permissions.
    ///
    /// @return a String array of all required permissions.
    String[] value();
}
