package com.github.kaktushose.jda.commands.permissions;

import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Default implementation of {@link PermissionsProvider} with the following behaviour:
 * <ul>
 *     <li>{@link PermissionsProvider#hasPermission(User, InvocationContext <?>)} will always return {@code true}</li>
 *     <li>
 *         {@link PermissionsProvider#hasPermission(Member, InvocationContext <?>)} will check against the default Discord permissions. More
 *         formally, this method will work with any permission provided by {@link Permission#values()}, ignoring the
 *         case. Any other permission String will be ignored.
 *     </li>
 * </ul>
 *
 * @see PermissionsProvider
 * @since 2.0.0
 */
public class DefaultPermissionsProvider implements PermissionsProvider {

    private static final Logger log = LoggerFactory.getLogger(DefaultPermissionsProvider.class);

    @Override
    public boolean hasPermission(@NotNull User user, @NotNull InvocationContext<?> context) {
        return true;
    }

    @Override
    public boolean hasPermission(@NotNull Member member, @NotNull InvocationContext<?> context) {
        for (String s : context.definition().getPermissions()) {
            // not a discord perm, continue
            if (Arrays.stream(Permission.values()).noneMatch(p -> p.name().equalsIgnoreCase(s))) {
                continue;
            }
            if (!member.hasPermission(Permission.valueOf(s.toUpperCase()))) {
                log.debug("{} permission is missing!", s.toUpperCase());
                return false;
            }
        }
        return true;
    }
}
