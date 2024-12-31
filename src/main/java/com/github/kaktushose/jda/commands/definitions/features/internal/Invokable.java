package com.github.kaktushose.jda.commands.definitions.features.internal;

import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.features.Replyable;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.SequencedCollection;

public sealed interface Invokable extends Definition permits Replyable, InteractionDefinition {

    Logger log = LoggerFactory.getLogger(Invokable.class);

    default Object invoke(@NotNull Object instance, @NotNull InvocationContext<?> invocation) throws InvocationTargetException, IllegalAccessException {
        SequencedCollection<Object> arguments = invocation.arguments();

        return methodDescription().invoke(instance, arguments);
    }

    @NotNull
    ClassDescription clazzDescription();

    @NotNull
    MethodDescription methodDescription();
}
