package com.github.kaktushose.jda.commands.reflect.interactions;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Classes that are intended to be executed using the
 * {@link com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor RuntimeSupervisor} must
 * be a subtype of InteractionRuntimeExecutable.
 *
 * <p>
 * This class acts as a wrapper for the underlying {@link Method} definition. It can be used to create new instances of
 * the declaring class. Furthermore, it creates the "definitionId", a unique identifier for every method wrapper, or
 * more formally for every subtype of InteractionRuntimeExecutable. The "definitionId" consists of the simple name of
 * the declaring class followed by the simple name of the method, e.g. {@code InteractionControlleronSlashCommand}.
 * </p>
 */
public abstract class InteractionRuntimeExecutable {

    protected final String definitionId;
    protected final Method method;

    protected InteractionRuntimeExecutable(Method method) {
        this.definitionId = String.format("%s%s", method.getDeclaringClass().getSimpleName(), method.getName());
        this.method = method;
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
