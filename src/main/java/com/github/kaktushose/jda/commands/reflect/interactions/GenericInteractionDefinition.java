package com.github.kaktushose.jda.commands.reflect.interactions;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Abstract base class for all interaction definitions.
 *
 * @since 4.0.0
 */
public abstract class GenericInteractionDefinition {

    public static final String ID_PREFIX = "jdac-";
    protected static final Logger log = LoggerFactory.getLogger(GenericInteractionDefinition.class);

    protected final String id;
    protected final Method method;
    protected final Set<String> permissions;

    protected GenericInteractionDefinition(Method method, Set<String> permissions) {
        this.id = String.format("%s%s.%s", ID_PREFIX, method.getDeclaringClass().getSimpleName(), method.getName());
        this.method = method;
        this.permissions = permissions;
    }

    /**
     * Returns the id of the interaction.
     *
     * @return the id of the interaction
     */
    @NotNull
    public String getId() {
        return id;
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
