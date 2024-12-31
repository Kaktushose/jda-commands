package com.github.kaktushose.jda.commands.definitions.interactions;

import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.features.internal.Invokable;
import com.github.kaktushose.jda.commands.definitions.features.Permissions;
import com.github.kaktushose.jda.commands.definitions.features.Replyable;
import com.github.kaktushose.jda.commands.definitions.interactions.command.CommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ComponentDefinition;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public sealed interface InteractionDefinition extends Definition, Invokable, Permissions, Replyable
        permits AutoCompleteDefinition, ComponentDefinition, ModalDefinition, CommandDefinition {

    @NotNull
    @Override
    default String definitionId() {
        return String.valueOf((clazzDescription().clazz().getName() + methodDescription().name()).hashCode());
    }

    default Object newInstance() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return clazzDescription().clazz().getConstructors()[0].newInstance();
    }
}
