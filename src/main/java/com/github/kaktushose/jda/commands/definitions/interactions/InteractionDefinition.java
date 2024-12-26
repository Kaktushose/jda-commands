package com.github.kaktushose.jda.commands.definitions.interactions;

import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.features.Invokeable;
import com.github.kaktushose.jda.commands.definitions.features.Permissions;
import com.github.kaktushose.jda.commands.definitions.features.Replyable;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.AutoCompleteDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.ComponentDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.ModalDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.command.CommandDefinition;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public sealed interface InteractionDefinition extends Definition, Invokeable, Permissions, Replyable
        permits AutoCompleteDefinition, ComponentDefinition, ModalDefinition, CommandDefinition {

    @NotNull
    @Override
    default String definitionId() {
        return String.valueOf((method().declaringClass().getName() + method().name()).hashCode());
    }

    default Object newInstance() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return clazz().clazz().getConstructors()[0].newInstance();
    }
}
