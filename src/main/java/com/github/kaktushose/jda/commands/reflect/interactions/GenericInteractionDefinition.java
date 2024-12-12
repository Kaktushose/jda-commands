package com.github.kaktushose.jda.commands.reflect.interactions;

import com.github.kaktushose.jda.commands.dispatching.Invocation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.SequencedCollection;
import java.util.Set;
import java.util.UUID;

/**
 * Abstract base class for all interaction definitions.
 *
 * @since 4.0.0
 */
public sealed abstract class GenericInteractionDefinition permits AutoCompleteDefinition, EphemeralInteractionDefinition {

    protected static final Logger log = LoggerFactory.getLogger(GenericInteractionDefinition.class);

    protected final String definitionId;
    protected final Method method;
    protected final Set<String> permissions;

    protected GenericInteractionDefinition(Method method, Set<String> permissions) {
        this.definitionId = UUID.randomUUID().toString();
        this.method = method;
        this.permissions = permissions;
    }

    public final void invoke(Invocation<?> invocation) {
        SequencedCollection<Object> arguments = invocation.arguments();

        log.info("Executing interaction {} for user {}", method.getName(), invocation.context().event().getMember());
        try {
            log.debug("Invoking method with following arguments: {}", arguments);
            method.invoke(invocation.instanceSupplier().apply(this), arguments.toArray());
        } catch (Exception exception) {
            log.error("Interaction execution failed!", exception);
            // this unwraps the underlying error in case of an exception inside the command class
            Throwable throwable = exception instanceof InvocationTargetException ? exception.getCause() : exception;
            invocation.context().cancel(invocation.context().implementationRegistry().getErrorMessageFactory().getCommandExecutionFailedMessage(invocation.context(), throwable));
        }
    }

    /**
     * Returns the id of the interaction definition.
     *
     * @return the id of the interaction definition
     */
    @NotNull
    public String getDefinitionId() {
        return definitionId;
    }

    /**
     * Gets the {@link Method} of the interaction.
     *
     * @return the {@link Method} of the interaction
     */
    @NotNull
    public Method getMethod() {
        return method;
    }

    /**
     * Gets a set of permission Strings.
     *
     * @return set of permission Strings
     */
    public Set<String> getPermissions() {
        return permissions;
    }

    /**
     * Returns the display name of this interaction. By default, returns the method name
     *
     * @return the name of this interaction
     */
    public String getDisplayName() {
        return method.getName();
    }

    /**
     * Gets a new instance of the method defining class
     *
     * @return a new instance of the method defining class
     * @throws InvocationTargetException if the underlying constructor throws an exception
     * @throws InstantiationException    if the class that declares the underlying constructor represents an abstract class
     * @throws IllegalAccessException    if this Constructor object is enforcing Java language access control and
     *                                   the underlying constructor is inaccessible
     */
    @NotNull
    public Object newInstance() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return method.getDeclaringClass().getConstructors()[0].newInstance();
    }
}
