package com.github.kaktushose.jda.commands.reflect.interactions;

import java.lang.reflect.Method;

/**
 * Represents interactions that can be replied to with an ephemeral text message.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @since 4.0.0
 */
public abstract class EphemeralInteraction extends GenericInteraction {

    protected boolean ephemeral;

    protected EphemeralInteraction(Method method, boolean ephemeral) {
        super(method);
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
