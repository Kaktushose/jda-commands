package com.github.kaktushose.jda.commands.reflect.interactions;

import com.github.kaktushose.jda.commands.reflect.interactions.commands.GenericCommandDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.components.GenericComponentDefinition;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Represents interactions that can be replied to with an ephemeral text message.
 *
 * @since 4.0.0
 */
public abstract sealed class EphemeralInteractionDefinition extends GenericInteractionDefinition permits ModalDefinition, GenericCommandDefinition, GenericComponentDefinition {

    protected boolean ephemeral;

    protected EphemeralInteractionDefinition(Method method, Set<String> permissions, boolean ephemeral) {
        super(method, permissions);
        this.ephemeral = ephemeral;
    }

    /**
     * Gets whether replies should be ephemeral.
     *
     * @return {@code true} if replies should be ephemeral
     */
    public boolean isEphemeral() {
        return ephemeral;
    }

    /**
     * Sets whether replies should be ephemeral.
     *
     * @param ephemeral {@code true} if replies should be ephemeral
     */
    public void setEphemeral(boolean ephemeral) {
        this.ephemeral = ephemeral;
    }

}
