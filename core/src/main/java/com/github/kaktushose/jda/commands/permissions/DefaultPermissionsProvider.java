package com.github.kaktushose.jda.commands.permissions;

import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/// Default implementation of [PermissionsProvider] with the following behaviour:
///
///   - [#hasPermission(User,InvocationContext)] will always return `true`
///   -
///     [#hasPermission(Member,InvocationContext)] will check against the default Discord permissions. More
///     formally, this method will work with any permission provided by [Permission#values()], ignoring the
///     case. Any other permission String will be ignored.
///
///
///
/// @see PermissionsProvider
public class DefaultPermissionsProvider implements PermissionsProvider {

    private static final Logger log = LoggerFactory.getLogger(DefaultPermissionsProvider.class);

    /// Doesn't perform checks and will always return `true`.
    ///
    /// @return always `true`
    @Override
    public boolean hasPermission(User user, InvocationContext<?> context) {
        return true;
    }

    /// Checks against the default Discord permissions.
    ///
    /// More formally, this method will work with any permission provided by [Permission#values()], case-insensitive.
    /// Any other permission String will be ignored.
    @Override
    public boolean hasPermission(Member member, InvocationContext<?> context) {
        for (String s : context.definition().permissions()) {
            // not a discord perm, continue
            if (Arrays.stream(Permission.values()).noneMatch(p -> p.name().equalsIgnoreCase(s))) {
                continue;
            }
            Helpers.checkDetached(member, DefaultPermissionsProvider.class);
            if (!member.hasPermission(Permission.valueOf(s.toUpperCase()))) {
                log.debug("{} permission is missing!", s.toUpperCase());
                return false;
            }
        }
        return true;
    }
}
