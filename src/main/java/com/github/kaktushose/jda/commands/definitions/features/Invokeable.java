package com.github.kaktushose.jda.commands.definitions.features;

import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.interactions.Interaction;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.SequencedCollection;

public sealed interface Invokeable permits Replyable, Interaction {

    Logger log = LoggerFactory.getLogger(Invokeable.class);

    default void invoke(@NotNull Object instance, @NotNull InvocationContext<?> invocation) throws Throwable {
        SequencedCollection<Object> arguments = invocation.arguments();

        method().invoke(instance, arguments);
    }

    @NotNull
    MethodDescription method();

    @NotNull
    SequencedCollection<Class<?>> methodSignature();

}
