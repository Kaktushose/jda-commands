package com.github.kaktushose.jda.commands.definitions.api.features;

import com.github.kaktushose.jda.commands.definitions.api.interactions.ButtonDefinition;
import com.github.kaktushose.jda.commands.definitions.api.interactions.CommandDefinition;
import com.github.kaktushose.jda.commands.definitions.api.interactions.ModalDefinition;
import com.github.kaktushose.jda.commands.definitions.api.interactions.SelectMenuDefinition;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.ReplyConfig;

public sealed interface Replyable permits ButtonDefinition, CommandDefinition, ModalDefinition, SelectMenuDefinition {

    ReplyConfig replyConfig();

}
