package com.github.kaktushose.jda.commands.dispatching.reply;

import net.dv8tion.jda.api.entities.Message;

public final class ComponentReply extends ConfigurableReply {

    public ComponentReply(ConfigurableReply reply) {
        super(reply);
    }

    public ComponentReply staticComponents(boolean staticComponents) {
        this.staticComponents = staticComponents;
        return this;
    }

    /**
     * Sends the reply to Discord.
     */
    public Message reply() {
        return complete();
    }

}
