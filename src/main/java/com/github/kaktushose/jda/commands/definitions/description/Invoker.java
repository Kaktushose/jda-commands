package com.github.kaktushose.jda.commands.definitions.description;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.SequencedCollection;

/// A functional interface, that allows the invocation of the described method
@FunctionalInterface
public interface Invoker {

    /// @param instance an instance of the declaring class of the method
    /// @param arguments a [SequencedCollection] of the arguments the method should be invoked with
    /// @return the result of the method invocation
    /// @throws IllegalAccessException if this Method object is enforcing Java language access control and the
    /// underlying method is inaccessible.
    /// @throws InvocationTargetException if an exception was thrown by the invoked method or constructor.
    @NotNull Object invoke(@NotNull Object instance, @NotNull SequencedCollection<Object> arguments) throws IllegalAccessException, InvocationTargetException;
}
