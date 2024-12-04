package com.github.kaktushose.jda.commands.dispatching.reply.components;

import com.github.kaktushose.jda.commands.dispatching.reply.Replyable;

/**
 * Marker interface for components that can be added to messages.
 *
 * @see Replyable#with(Component...) ReplyAction#with(Component...)
 * @see Buttons
 * @see SelectMenus
 * @since 2.3.0
 */
public sealed interface Component permits Buttons, SelectMenus {
}
