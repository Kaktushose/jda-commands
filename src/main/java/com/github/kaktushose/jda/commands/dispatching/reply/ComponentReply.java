package com.github.kaktushose.jda.commands.dispatching.reply;

import net.dv8tion.jda.api.entities.Message;

public final class ComponentReply extends ConfigurableReply {

    public ComponentReply(ConfigurableReply reply) {
        super(reply);
    }

    /**
     * Sends the reply to Discord.
     */
    public Message reply() {
        return complete();
    }

}
