package com.github.kaktushose.jda.commands.rewrite.dispatching.parser.impl;

import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.rewrite.dispatching.parser.Parser;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MessageParser extends Parser<MessageReceivedEvent> {

    @Override
    public CommandContext parse(MessageReceivedEvent event) {
        return null;
    }
}
