package com.github.kaktushose.jda.commands.definitions.description;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.SequencedCollection;

/// A [Description] that describes a method.
///
///  @param declaringClass the declaring [Class] of this method
/// @param returnType the [Class] this method returns
/// @param name the name of the method
/// @param parameters a [SequencedCollection] of the [ParameterDescription]s of this method
/// @param annotations a [Collection] of all [Annotation]s this method is annotated with
/// @param invoker the corresponding
public record MethodDescription(
        @NotNull Class<?> declaringClass,
        @NotNull Class<?> returnType,
        @NotNull String name,
        @NotNull SequencedCollection<ParameterDescription> parameters,
        @NotNull Collection<Annotation> annotations,
        @NotNull Invoker invoker
) implements Description {

    /// @param instance an instance of the declaring class of the method
    /// @param arguments a [SequencedCollection] of the arguments the method should be invoked with
    /// @return the result of the method invocation
    /// @throws IllegalAccessException if this Method object is enforcing Java language access control and the
    /// underlying method is inaccessible.
    /// @throws InvocationTargetException if an exception was thrown by the invoked method or constructor.
    public Object invoke(@NotNull Object instance, @NotNull SequencedCollection<Object> arguments) throws IllegalAccessException, InvocationTargetException {
        return invoker.invoke(instance, arguments);
    }
}
