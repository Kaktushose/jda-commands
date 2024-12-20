package com.github.kaktushose.jda.commands.dispatching.reply;

import com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig;

/**
 * Will be used if no {@link ReplyConfig Reply} annotation is
 * present.
 *
 */
public class GlobalReplyConfig {

    /// Whether to send ephemeral replies. Default value is `false`.
    ///
    /// Ephemeral messages have some limitations and will be removed once the user restarts their client.
    /// Limitations:
    /// - Cannot contain any files/ attachments
    /// - Cannot be reacted to
    /// - Cannot be retrieved
    public static boolean ephemeral = false;

    /// Whether to keep the original components when editing a message. Default value is `true`.
    ///
    /// More formally, if editing a message and `keepComponents` is `true`, the original message will first be queried and
    /// its components get added to the reply before it is sent.
    public static boolean keepComponents = true;

    /// Whether to edit the original message or to send a new one. Default value is `true`.
    ///
    /// The original message is always the very first reply that was sent. E.g. for a slash command event, which was
    /// replied to with a text message and a button, the original message is that very reply.
    ///
    /// Subsequent replies to the same slash command event or the button event cannot be edited.
    public static boolean editReply = true;

}
