package com.github.kaktushose.jda.commands.permissions;

import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.extension.Implementation;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

/// Interface for performing permission checks.
///
/// @see DefaultPermissionsProvider
/// @see Permissions Permission
public non-sealed interface PermissionsProvider extends Implementation.ExtensionImplementable {

    /// Checks if a [User] has permissions. Compared to [#hasPermission(Member, InvocationContext)] this method will be
    /// called if the command gets executed in a non-guild context, where no member object is available.
    ///
    /// @param user    the [User] to perform the check against
    /// @param context the corresponding [InvocationContext]
    /// @return `true` if the user has the permission to execute the command
    /// @see #hasPermission(Member, InvocationContext)
    boolean hasPermission(@NotNull User user, @NotNull InvocationContext<?> context);

    /// Checks if a [Member] has permissions.
    ///
    /// @param member  the [Member] to perform the check against
    /// @param context the corresponding [InvocationContext]
    /// @return `true` if the user has the permission to execute the command
    boolean hasPermission(@NotNull Member member, @NotNull InvocationContext<?> context);

}
