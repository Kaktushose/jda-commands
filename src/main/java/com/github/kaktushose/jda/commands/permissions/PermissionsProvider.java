package com.github.kaktushose.jda.commands.permissions;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

/**
 * Interface for performing permission checks.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see DefaultPermissionsProvider
 * @see com.github.kaktushose.jda.commands.annotations.Permission Permission
 * @since 2.0.0
 */
public interface PermissionsProvider {

    /**
     * Checks if a {@link User} is muted. Useful for ban lists, where the {@link CommandContext} should be cancelled even before
     * routing, permission checks, etc.
     *
     * @param user    the {@link User} to perform the check against
     * @param context the corresponding {@link CommandContext}
     * @return {@code true} if the user is muted
     * @see com.github.kaktushose.jda.commands.dispatching.filter.impl.UserMuteFilter UserMuteFilter
     */
    boolean isMuted(User user, CommandContext context);

    /**
     * Checks if a {@link User} has permissions. Compared to {@link #hasPermission(Member, CommandContext)} this method will be
     * called if the command gets executed in a non-guild context, where no member object is available.
     *
     * @param user    the {@link User} to perform the check against
     * @param context the corresponding {@link CommandContext}
     * @return {@code true} if the user has the permission to execute the command
     * @see #hasPermission(Member, CommandContext)
     */
    boolean hasPermission(User user, CommandContext context);

    /**
     * Checks if a {@link Member} has permissions.
     *
     * @param member  the {@link Member} to perform the check against
     * @param context the corresponding {@link CommandContext}
     * @return {@code true} if the user has the permission to execute the command
     */
    boolean hasPermission(Member member, CommandContext context);

}
