package com.github.kaktushose.jda.commands.dispatching.reply;

import com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig;

/**
 * Will be used if no {@link ReplyConfig Reply} annotation is
 * present.
 *
 */
public class GlobalReplyConfig {

    public static boolean ephemeral = false;
    public static boolean keepComponents = true;
    public static boolean editReply = true;

}
