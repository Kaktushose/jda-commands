package com.github.kaktushose.jda.commands.definitions.features.internal;

import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.handling.EventHandler;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.SequencedCollection;

/// Indicates that the implementing [Definition] is bound to a method that can be invoked.
@ApiStatus.Internal
public sealed interface Invokable extends Definition permits InteractionDefinition {

    Logger log = LoggerFactory.getLogger(Invokable.class);

    /// Invokes the method that this [Definition] is bound to.
    ///
    /// @param instance   an instance of the declaring class of the method
    /// @param invocation the corresponding [InvocationContext]
    /// @return the result of the method invocation
    /// @throws IllegalStateException     if the definition must not be invoked at the moment this method gets called
    /// @throws IllegalAccessException    if this Method object is enforcing Java language access control and the
    ///                                   underlying method is inaccessible.
    /// @throws InvocationTargetException if an exception was thrown by the invoked method or constructor.
    @Nullable
    default Object invoke(@NotNull Object instance, @NotNull InvocationContext<?> invocation) throws IllegalAccessException, InvocationTargetException {
        if (!EventHandler.INVOCATION_PERMITTED.get()) {
            throw new IllegalStateException("The Definition must not be invoked at this point.");
        }
        SequencedCollection<Object> arguments = invocation.arguments();

        EventHandler.INVOCATION_PERMITTED.set(false);
        return methodDescription().invoker().invoke(instance, arguments);
    }

    /// The [ClassDescription] of the declaring class of the [#methodDescription()].
    @NotNull
    ClassDescription clazzDescription();

    /// The [MethodDescription] of the method this [Definition] is bound to.
    @NotNull
    MethodDescription methodDescription();
}
