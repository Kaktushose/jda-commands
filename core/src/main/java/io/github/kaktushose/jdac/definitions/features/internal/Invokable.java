package io.github.kaktushose.jdac.definitions.features.internal;

import io.github.kaktushose.jdac.definitions.Definition;
import io.github.kaktushose.jdac.definitions.description.ClassDescription;
import io.github.kaktushose.jdac.definitions.description.MethodDescription;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.dispatching.handling.EventHandler;
import io.github.kaktushose.jdac.exceptions.InternalException;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
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
    /// @throws IllegalAccessException    if the method object is enforcing Java language access control and the
    ///                                   underlying method is inaccessible
    /// @throws InvocationTargetException if an exception was thrown by the invoked method or constructor
    @Nullable
    default Object invoke(Object instance, InvocationContext<?> invocation) throws IllegalAccessException, InvocationTargetException {
        if (!EventHandler.INVOCATION_PERMITTED.orElse(false)) {
            throw new InternalException("invocation-not-permitted");
        }
        SequencedCollection<Object> arguments = invocation.rawArguments();

        // ScopedValue#call uses a generic for the exception, thus we have to handle the most common type between IllegalAccessException and InvocationTargetException
        try {
            return ScopedValue.where(EventHandler.INVOCATION_PERMITTED, false).call(() -> methodDescription().invoker().invoke(instance, arguments));
        } catch (IllegalAccessException | InternalException | InvocationTargetException e) {
            throw e;
        } catch (ReflectiveOperationException e) {
            throw new InternalException("should-never-be-thrown", e);
        }
    }

    /// The [ClassDescription] of the declaring class of the [#methodDescription()].
    ClassDescription classDescription();

    /// The [MethodDescription] of the method this [Definition] is bound to.
    MethodDescription methodDescription();
}
