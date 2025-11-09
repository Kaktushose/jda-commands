package io.github.kaktushose.jdac.annotations.interactions;

import io.github.kaktushose.jdac.dispatching.middleware.impl.PermissionsMiddleware;
import io.github.kaktushose.jdac.permissions.PermissionsProvider;
import net.dv8tion.jda.api.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// [Interaction] classes or
/// interaction methods (commands, components or modals) annotated with Permission will
/// require the user to have the given permissions in order to execute the command.
///
/// @apiNote This annotation should not be confused with [CommandConfig#enabledFor()].
/// The `enabledFor` permissions will be client-side checked by Discord directly, while the [Permissions] annotation
/// will be used for the own permission system of jda-commands.
/// @implNote The [PermissionsMiddleware] will validate the permissions during the middleware phase of the execution
/// chain. The [PermissionsProvider] will be called to check the respective user. The default implementation of the
/// [PermissionsProvider] can only handle Discord permissions([Permission]).
///
/// ## Example:
/// ```
/// @Permissions("BAN_MEMBERS")
/// public class BanCommand { ... }
/// ```
///
/// @see PermissionsProvider
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Permissions {

    /// Returns a String array of all required permissions.
    ///
    /// @return a String array of all required permissions.
    String[] value();
}
