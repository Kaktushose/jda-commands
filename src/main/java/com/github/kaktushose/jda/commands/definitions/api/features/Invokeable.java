package com.github.kaktushose.jda.commands.definitions.api.features;

import com.github.kaktushose.jda.commands.definitions.api.interactions.AutoCompleteDefinition;
import com.github.kaktushose.jda.commands.definitions.api.interactions.PermissionsInteraction;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.SequencedCollection;

public sealed interface Invokeable permits AutoCompleteDefinition, PermissionsInteraction {

    Logger log = LoggerFactory.getLogger(Invokeable.class);

    void invoke(Object instance, InvocationContext<?> invocation) throws InvocationTargetException, IllegalAccessException;

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
