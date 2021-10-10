package com.github.kaktushose.jda.commands.embeds;

import com.github.kaktushose.jda.commands.reflect.CommandDefinition;
import com.github.kaktushose.jda.commands.reflect.ControllerDefinition;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.util.Set;

public class DefaultHelpMessageFactory implements HelpMessageFactory {

    @Override
    public Message getSpecificHelp(CommandDefinition command) {
        return new MessageBuilder().append("specific help").build();
    }

    @Override
    public Message getGenericHelp(Set<ControllerDefinition> controllers) {
        return new MessageBuilder().append("generic help").build();
    }
}
