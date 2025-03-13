package com.github.kaktushose.jda.commands.dispatching.reply.dynamic;

import com.github.kaktushose.jda.commands.definitions.interactions.component.ComponentDefinition;

public sealed interface ComponentBuilder permits ButtonBuilder, SelectMenuBuilder {

    ComponentDefinition<?> build();

}
