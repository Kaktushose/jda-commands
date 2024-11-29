package com.github.kaktushose.jda.commands.reflect.interactions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Abstract base class for all interaction definitions.
 *
 * @since 4.0.0
 */
public abstract class GenericInteractionDefinition extends InteractionRuntimeExecutable {

    protected static final Logger log = LoggerFactory.getLogger(GenericInteractionDefinition.class);
    protected final Set<String> permissions;

    protected GenericInteractionDefinition(Method method, Set<String> permissions) {
        super(method);
        this.permissions = permissions;
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
}
