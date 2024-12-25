package com.github.kaktushose.jda.commands.definitions.description;

import java.lang.reflect.InvocationTargetException;
import java.util.SequencedCollection;

public interface Invoker {
    Object invoke(Object instance, SequencedCollection<Object> arguments) throws IllegalAccessException, InvocationTargetException;
}
