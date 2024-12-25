package com.github.kaktushose.jda.commands.definitions.features;

import com.github.kaktushose.jda.commands.definitions.interactions.impl.*;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.ReplyConfig;

public sealed interface Replyable permits ButtonDefinition, CommandDefinition, ModalDefinition, SelectMenuDefinition, SlashCommandDefinition {

    ReplyConfig replyConfig();

}
