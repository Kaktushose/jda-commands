package io.github.kaktushose.jdac.definitions.description;


import org.jspecify.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.SequencedCollection;

/// A functional interface, that allows the invocation of a [MethodDescription].
@FunctionalInterface
public interface Invoker {

    /// Invokes the method behind [MethodDescription].
    ///
    /// **Please note that this method isn't intended to be directly used by bot developers.
    /// Calling it may result in undefined behavior!**
    ///
    /// @param instance  an instance of the declaring class of the method
    /// @param arguments a [SequencedCollection] of the arguments the method should be invoked with
    /// @return the result of the method invocation
    /// @throws IllegalAccessException    if this Method object is enforcing Java language access control and the
    ///                                   underlying method is inaccessible
    /// @throws InvocationTargetException if an exception was thrown by the invoked method or constructor
    @Nullable
    Object invoke(Object instance, SequencedCollection<Object> arguments) throws IllegalAccessException, InvocationTargetException;
}
