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

    protected final ReplyConfig replyConfig;

    protected EphemeralInteractionDefinition(Method method, Set<String> permissions, ReplyConfig replyConfig) {
        super(method, permissions);
        this.replyConfig = replyConfig;
    }

    /**
     * Gets whether replies should be ephemeral.
     *
     * @return {@code true} if replies should be ephemeral
     */
    public ReplyConfig replyConfig() {
        return replyConfig;
    }

}
