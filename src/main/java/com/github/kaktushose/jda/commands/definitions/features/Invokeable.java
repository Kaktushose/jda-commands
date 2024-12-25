package com.github.kaktushose.jda.commands.definitions.features;

import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.interactions.Interaction;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.AutoCompleteDefinition;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.SequencedCollection;

public sealed interface Invokeable permits Permissions, Replyable, Interaction, AutoCompleteDefinition {

    Logger log = LoggerFactory.getLogger(Invokeable.class);

    default Object invoke(@NotNull Object instance, @NotNull InvocationContext<?> invocation) throws InvocationTargetException, IllegalAccessException {
        SequencedCollection<Object> arguments = invocation.arguments();

        return method().invoke(instance, arguments);
    }

    @NotNull
    ClassDescription clazz();

    @NotNull
    MethodDescription method();

    @NotNull
    SequencedCollection<Class<?>> methodSignature();

}
