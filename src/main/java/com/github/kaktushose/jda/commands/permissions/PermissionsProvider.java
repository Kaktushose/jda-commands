package com.github.kaktushose.jda.commands.permissions;

import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.refactor.context.ExecutionContext;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for performing permission checks.
 *
 * @see com.github.kaktushose.jda.commands.annotations.Implementation
 * @see DefaultPermissionsProvider
 * @see Permissions Permission
 * @since 2.0.0
 */
public interface PermissionsProvider {

    /**
     * Checks if a {@link User} has permissions. Compared to {@link #hasPermission(Member, Context)} this method will be
     * called if the command gets executed in a non-guild context, where no member object is available.
     *
     * @param user    the {@link User} to perform the check against
     * @param context the corresponding {@link Context}
     * @return {@code true} if the user has the permission to execute the command
     * @see #hasPermission(Member, Context)
     */
    boolean hasPermission(@NotNull User user, @NotNull ExecutionContext<?, ?> context);

    /**
     * Checks if a {@link Member} has permissions.
     *
     * @param member  the {@link Member} to perform the check against
     * @param context the corresponding {@link Context}
     * @return {@code true} if the user has the permission to execute the command
     */
    boolean hasPermission(@NotNull Member member, @NotNull ExecutionContext<?, ?> context);

}
