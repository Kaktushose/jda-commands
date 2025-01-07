package com.github.kaktushose.jda.commands.definitions.interactions;

import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.description.ParameterDescription;
import com.github.kaktushose.jda.commands.definitions.features.CustomIdJDAEntity;
import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ModalEvent;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/// Representation of a modal.
///
/// @param clazzDescription  the [ClassDescription] of the declaring class of the [#methodDescription()]
/// @param methodDescription the [MethodDescription] of the method this definition is bound to
/// @param permissions       a [Collection] of permissions for this modal
/// @param title             the title of the modal
/// @param textInputs        the [`TextInputs`][TextInputDefinition] of this modal
public record ModalDefinition(
        @NotNull ClassDescription clazzDescription,
        @NotNull MethodDescription methodDescription,
        @NotNull Collection<String> permissions,
        @NotNull String title,
        @NotNull SequencedCollection<TextInputDefinition> textInputs
) implements InteractionDefinition, CustomIdJDAEntity<Modal> {

    /// Builds a new [ModalDefinition] from the given [MethodBuildContext].
    ///
    /// @return an [Optional] holding the [ModalDefinition]
    public static Optional<ModalDefinition> build(MethodBuildContext context) {
        var method = context.method();
        var modal = method.annotation(com.github.kaktushose.jda.commands.annotations.interactions.Modal.class).orElseThrow();

        // Modals support up to 5 TextInputs
        if (method.parameters().isEmpty() || method.parameters().size() > 6) {
            log.error("An error has occurred! Skipping Modal {}.{}:",
                    method.declaringClass().getName(),
                    method.name(),
                    new IllegalArgumentException("Invalid amount of parameters! Modals need between 1 and 5 TextInputs"));
            return Optional.empty();
        }

        if (Helpers.isIncorrectParameterType(method, 0, ModalEvent.class)) {
            return Optional.empty();
        }

        List<TextInputDefinition> textInputs = new ArrayList<>();
        for (int i = 1; i < method.parameters().size(); i++) {
            var parameter = List.copyOf(method.parameters()).get(i);
            TextInputDefinition.build(parameter).ifPresent(textInputs::add);
        }

        if (textInputs.isEmpty()) {
            log.error("An error has occurred! Skipping Modal {}.{}:",
                    method.declaringClass().getName(),
                    method.name(),
                    new IllegalArgumentException("Modals need at least one valid TextInput"));
            return Optional.empty();
        }

        List<Class<?>> signature = new ArrayList<>();
        signature.add(ModalEvent.class);
        textInputs.forEach(_ -> signature.add(String.class));
        if (Helpers.checkSignature(method, signature)) {
            return Optional.empty();
        }


        return Optional.of(new ModalDefinition(context.clazz(), method, Helpers.permissions(context), modal.value(), textInputs));
    }

    /// Transforms this definition to an [Modal] with the given custom id.
    ///
    /// @return the [Modal]
    /// @see CustomId#independent(String)
    @NotNull
    @Override
    public Modal toJDAEntity(@NotNull CustomId customId) {
        var modal = Modal.create(customId.id(), title);

        textInputs.forEach(textInput -> modal.addActionRow(textInput.toJDAEntity()));

        return modal.build();
    }

    @NotNull
    @Override
    public String displayName() {
        return title;
    }

    /// Representation of a modal text input defined by
    /// [`TextInput`][com.github.kaktushose.jda.commands.annotations.interactions.TextInput]
    public record TextInputDefinition(
            @NotNull String label,
            @NotNull String placeholder,
            @NotNull String defaultValue,
            int minValue,
            int maxValue,
            @NotNull TextInputStyle style,
            boolean required
    ) implements JDAEntity<TextInput>, Definition {

        /// Builds a new [TextInputDefinition] from the given [ParameterDescription]
        ///
        /// @param parameter the [ParameterDescription] to build the [TextInputDefinition] from
        /// @return the new [TextInputDefinition]
        public static Optional<TextInputDefinition> build(ParameterDescription parameter) {
            var optional = parameter.annotation(com.github.kaktushose.jda.commands.annotations.interactions.TextInput.class);

            if (optional.isEmpty()) {
                return Optional.empty();
            }

            // TODO for now we only allow Strings, maybe add type adapting in the future
            if (!String.class.isAssignableFrom(parameter.type())) {
                return Optional.empty();
            }
            var textInput = optional.get();

            return Optional.of(new TextInputDefinition(
                    textInput.label().isEmpty() ? parameter.name() : textInput.label(),
                    textInput.value(),
                    textInput.defaultValue(),
                    textInput.minValue(),
                    textInput.maxValue(),
                    textInput.style(),
                    textInput.required()
            ));
        }

        @Override
        public @NotNull String displayName() {
            return label;
        }

        /// Transforms this definition into a [TextInput].
        ///
        /// @return the [TextInput]
        @NotNull
        @Override
        public TextInput toJDAEntity() {
            var textInput = TextInput.create(label, label, style).setRequired(required);

            if (minValue != -1) {
                textInput.setMinLength(minValue);
            }
            if (maxValue != -1) {
                textInput.setMaxLength(maxValue);
            }
            if (!placeholder.isBlank()) {
                textInput.setPlaceholder(placeholder);
            }
            if (!defaultValue.isBlank()) {
                textInput.setValue(defaultValue);
            }

            return textInput.build();
        }
    }
}
