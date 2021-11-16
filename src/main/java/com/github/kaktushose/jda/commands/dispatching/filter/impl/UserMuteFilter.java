package com.github.kaktushose.jda.commands.dispatching.filter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.filter.Filter;
import com.github.kaktushose.jda.commands.permissions.PermissionsProvider;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link Filter} implementation that will check permissions similar to the {@link PermissionsFilter}. that gets executed at
 * The difference is that this filter gets executed at
 * {@link com.github.kaktushose.jda.commands.dispatching.filter.FilterRegistry.FilterPosition#BEFORE_ROUTING FilterPosition.BEFORE_ROUTING}.
 * This is necessary to achieve a clean implementation of ban lists. If a user is banned, he must not be able to
 * interact with the bot regardless of other constraints such as syntax errors, cooldown or parameter validation.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see PermissionsProvider#isMuted(User, CommandContext)
 * @since 2.0.0
 */
public class UserMuteFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(UserMuteFilter.class);

    /**
     * Checks if a {@link User} is muted and will cancel the {@link CommandContext} if he is.
     *
     * @param context the {@link CommandContext} to filter
     */
    @Override
    public void apply(CommandContext context) {
        log.debug("Checking mutes...");
        PermissionsProvider provider = context.getImplementationRegistry().getPermissionsProvider();

        if (provider.isMuted(context.getEvent().getAuthor(), context)) {
            context.setCancelled(true);
            context.setErrorMessage(context
                    .getImplementationRegistry()
                    .getErrorMessageFactory()
                    .getUserMutedMessage(context)
            );
            log.debug("Insufficient permissions - User is muted!");
            return;
        }

        log.debug("All mute checks passed");
    }
}
