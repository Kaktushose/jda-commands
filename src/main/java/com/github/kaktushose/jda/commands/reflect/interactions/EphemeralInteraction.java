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

    public boolean isEphemeral() {
        return ephemeral;
    }

    public void setEphemeral(boolean ephemeral) {
        this.ephemeral = ephemeral;
    }

}
