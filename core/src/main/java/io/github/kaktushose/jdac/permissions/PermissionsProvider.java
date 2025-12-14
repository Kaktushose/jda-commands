package io.github.kaktushose.jdac.permissions;

import io.github.kaktushose.jdac.annotations.interactions.Permissions;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

/// Interface for performing permission checks.
///
/// @see DefaultPermissionsProvider
/// @see Permissions Permission
public interface PermissionsProvider {

    /// Checks if a [User] has permissions. Compared to [#hasPermission(Member, InvocationContext)] this method will be
    /// called if the command gets executed in a non-guild context, where no member object is available.
    ///
    /// @param user    the [User] to perform the check against
    /// @param context the corresponding [InvocationContext]
    /// @return `true` if the user has the permission to execute the command
    /// @see #hasPermission(Member, InvocationContext)
    boolean hasPermission(User user, InvocationContext<?> context);

    /// Checks if a [Member] has permissions.
    ///
    /// @param member  the [Member] to perform the check against
    /// @param context the corresponding [InvocationContext]
    /// @return `true` if the user has the permission to execute the command
    boolean hasPermission(Member member, InvocationContext<?> context);

}
