package com.github.kaktushose.jda.commands.dispatching.reply;

public final class ComponentReply extends ConfigurableReply {

    public ComponentReply(ConfigurableReply reply) {
        super(reply);
    }

    /**
     * Sends the reply to Discord.
     */
    public void reply() {
        queue();
    }

}
