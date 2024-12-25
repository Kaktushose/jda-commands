package com.github.kaktushose.jda.commands.definitions.features;

import com.github.kaktushose.jda.commands.definitions.interactions.Interaction;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.SequencedCollection;

public sealed interface Invokeable permits Interaction {

    Logger log = LoggerFactory.getLogger(Invokeable.class);

    default void invoke(Object instance, InvocationContext<?> invocation) throws Throwable {
        SequencedCollection<Object> arguments = invocation.arguments();

        method().invoke(instance, arguments.toArray());
    }

    Method method();

    SequencedCollection<Class<?>> parameters();

    default boolean checkSignature() {
        if (method().getParameterCount() != parameters().size()) {
            log.error("Incorrect parameter count!");
            return false;
        }
        if (!Arrays.stream(method().getParameters()).map(Parameter::getType).equals(parameters())) {
            log.error("Incorrect parameter type!");
            return false;
        }
        return true;
    }

}
