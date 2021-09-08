package com.github.kaktushose.jda.commands.rewrite.dispatching.parser.impl;

import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.rewrite.dispatching.parser.Parser;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.regex.Pattern;

public class MessageParser extends Parser<MessageReceivedEvent> {

    @Override
    public CommandContext parse(MessageReceivedEvent event) {
        String contentRaw = event.getMessage().getContentRaw();

        while (contentRaw.contains("  ")) {
            contentRaw = contentRaw.replaceAll(" {2}", " ");
        }

        contentRaw = contentRaw.replaceFirst(Pattern.quote("!"), "").trim();

        String[] input = contentRaw.split(" ");

        return new CommandContext().setInput(input).setEvent(event);
    }
}
