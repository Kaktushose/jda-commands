package com.github.kaktushose.jda.commands.reflect.interactions;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.Modal;
import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.SlashCommandContext;
import com.github.kaktushose.jda.commands.dispatching.interactions.modals.ModalEvent;
import com.github.kaktushose.jda.commands.reflect.TextInputDefinition;
import net.dv8tion.jda.api.interactions.modals.Modal.Builder;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Representation of a Modal.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @see Modal
 * @since 4.0.0
 */
public class ModalDefinition extends EphemeralInteractionDefinition {

    private final String title;
    private final List<TextInputDefinition> textInputs;

    protected ModalDefinition(Method method, Set<String> permissions, boolean ephemeral, String title, List<TextInputDefinition> textInputs) {
        super(method, permissions, ephemeral);
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
        if (!method.isAnnotationPresent(Modal.class) || !method.getDeclaringClass().isAnnotationPresent(Interaction.class)) {
            return Optional.empty();
        }

        // Modals support up to 5 TextInputs
        if (method.getParameters().length < 1 || method.getParameters().length > 6) {
            log.error("An error has occurred! Skipping Modal {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException("Invalid amount of parameters!"));
            return Optional.empty();
        }

        if (!ModalEvent.class.isAssignableFrom(method.getParameters()[0].getType())) {
            log.error("An error has occurred! Skipping Modal {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException(String.format("First parameter must be of type %s!", ModalEvent.class.getSimpleName())));
            return Optional.empty();
        }

        List<TextInputDefinition> textInputs = new ArrayList<>();
        for (int i = 1; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];
            TextInputDefinition.build(parameter).ifPresent(textInputs::add);
        }

        if (textInputs.isEmpty()) {
            log.error("An error has occurred! Skipping Modal {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException("Modals need at least one valid TextInput"));
            return Optional.empty();
        }

        Set<String> permissions = new HashSet<>();
        if (method.isAnnotationPresent(Permissions.class)) {
            Permissions permission = method.getAnnotation(Permissions.class);
            permissions = new HashSet<>(Arrays.asList(permission.value()));
        }

        Modal modal = method.getAnnotation(Modal.class);

        return Optional.of(new ModalDefinition(method, permissions, modal.ephemeral(), modal.value(), textInputs));
    }

    /**
     * Transforms this ModalDefinition to a {@link net.dv8tion.jda.api.interactions.modals.Modal Modal}.
     *
     * @param id the id to use, see {@link #getRuntimeId(Context)}
     * @return the transformed {@link net.dv8tion.jda.api.interactions.modals.Modal Modal}
     */
    public net.dv8tion.jda.api.interactions.modals.Modal toModal(String id) {
        Builder modal = net.dv8tion.jda.api.interactions.modals.Modal.create(id, title);

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

    /**
     * Gets the runtime id. The runtime id is composed of the static interaction id and the
     * snowflake id of the interaction event that created the runtime.
     *
     * @param context the {@link SlashCommandContext} this button will be attached to
     * @return the runtime id
     */
    @NotNull
    public String getRuntimeId(Context context) {
        return String.format("%s.%s", getId(), context.getRuntime().getInstanceId());
    }

    @Override
    public String toString() {
        return "ModalDefinition{" +
                "title='" + title + '\'' +
                ", textInputs=" + textInputs +
                ", ephemeral=" + ephemeral +
                ", id='" + id + '\'' +
                ", method=" + method +
                ", permissions=" + permissions +
                '}';
    }
}
