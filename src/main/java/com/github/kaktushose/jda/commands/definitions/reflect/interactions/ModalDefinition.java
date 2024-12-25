package com.github.kaktushose.jda.commands.definitions.reflect.interactions;

import com.github.kaktushose.jda.commands.internal.Helpers;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.Modal;
import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ModalEvent;
import com.github.kaktushose.jda.commands.dispatching.internal.Runtime;
import com.github.kaktushose.jda.commands.definitions.reflect.misc.TextInputDefinition;
import net.dv8tion.jda.api.interactions.modals.Modal.Builder;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Representation of a Modal.
 *
 * @see Modal
 * @since 4.0.0
 */
public final class ModalDefinition extends EphemeralInteractionDefinition implements CustomId {

    private final String title;
    private final List<TextInputDefinition> textInputs;

    private ModalDefinition(Method method, Set<String> permissions, ReplyConfig replyConfig, String title, List<TextInputDefinition> textInputs) {
        super(method, permissions, replyConfig);
        this.title = title;
        this.textInputs = textInputs;
    }

    /**
     * Builds a new ModalDefinition.
     *
     * @param method the {@link Method} of the Modal
     * @return an {@link Optional} holding the ModalDefinition
     */
    public static Optional<ModalDefinition> build(@NotNull Method method) {

    }

    /**
     * Transforms this ModalDefinition to a {@link net.dv8tion.jda.api.interactions.modals.Modal Modal}.
     *
     * @param runtimeId the runtimeId of the
     *                  {@link Runtime Runtime}
     *                  of this interaction execution
     * @return the transformed {@link net.dv8tion.jda.api.interactions.modals.Modal Modal}
     */
    public net.dv8tion.jda.api.interactions.modals.Modal toModal(String runtimeId) {
        Builder modal = net.dv8tion.jda.api.interactions.modals.Modal.create(boundCustomId(runtimeId), title);

        textInputs.forEach(textInput -> modal.addActionRow(textInput.toTextInput()));

        return modal.build();
    }

    /**
     * Gets the title of the modal.
     *
     * @return the title of the modal
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets all text inputs this modal uses
     *
     * @return a list of {@link TextInputDefinition TextInputDefinitions}
     */
    public List<TextInputDefinition> getTextInputs() {
        return textInputs;
    }

    @Override
    public String getDisplayName() {
        return title;
    }

    @Override
    public @NotNull String boundCustomId(@NotNull String runtimeId) {
        return "%s.%s.%s".formatted(PREFIX, runtimeId, definitionId);
    }

    @Override
    public @NotNull String independentCustomId() {
        throw new UnsupportedOperationException("Modals cannot be independent!");
    }

    @Override
    public String toString() {
        return "ModalDefinition{" +
                "title='" + title + '\'' +
                ", textInputs=" + textInputs +
                ", replyConfig=" + replyConfig +
                ", id='" + definitionId + '\'' +
                ", method=" + method +
                ", permissions=" + permissions +
                '}';
    }
}
