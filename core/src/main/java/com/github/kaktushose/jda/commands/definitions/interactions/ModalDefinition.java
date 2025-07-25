package com.github.kaktushose.jda.commands.definitions.interactions;

import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.description.ParameterDescription;
import com.github.kaktushose.jda.commands.definitions.features.CustomIdJDAEntity;
import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ComponentDefinition;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ModalEvent;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/// Representation of a modal.
///
/// @param classDescription  the [ClassDescription] of the declaring class of the [#methodDescription()]
/// @param methodDescription the [MethodDescription] of the method this definition is bound to
/// @param permissions       a [Collection] of permissions for this modal
/// @param title             the title of the modal
/// @param textInputs        the [`TextInputs`][TextInputDefinition] of this modal
public record ModalDefinition(
        @NonNull ClassDescription classDescription,
        @NonNull MethodDescription methodDescription,
        @NonNull Collection<String> permissions,
        @NonNull String title,
        @NonNull SequencedCollection<TextInputDefinition> textInputs
) implements InteractionDefinition, CustomIdJDAEntity<Modal> {

    /// Builds a new [ModalDefinition] with the given values.
    public ModalDefinition with(@Nullable String title, @Nullable List<TextInputDefinition> textInputs) {
        return new ModalDefinition(
                classDescription,
                methodDescription,
                permissions,
                ComponentDefinition.override(this.title, title),
                ComponentDefinition.override(ArrayList::new, this.textInputs, textInputs)
        );
    }

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
    @NonNull
    public Modal toJDAEntity(@NonNull CustomId customId) {
        var modal = Modal.create(customId.merged(), title);

        textInputs.forEach(textInput -> modal.addActionRow(textInput.toJDAEntity()));

        return modal.build();
    }

    @NonNull
    @Override
    public String displayName() {
        return title;
    }

    /// Representation of a modal text input defined by
    /// [`TextInput`][com.github.kaktushose.jda.commands.annotations.interactions.TextInput]
    public record TextInputDefinition(
            @NonNull ParameterDescription parameter,
            @NonNull String label,
            @Nullable String placeholder,
            @Nullable String defaultValue,
            int minValue,
            int maxValue,
            @NonNull TextInputStyle style,
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
                    parameter,
                    textInput.value().isEmpty() ? parameter.name() : textInput.value(),
                    textInput.placeholder(),
                    textInput.defaultValue(),
                    textInput.minValue(),
                    textInput.maxValue(),
                    textInput.style(),
                    textInput.required()
            ));
        }

        /// Builds a new [TextInputDefinition] from the given [TextInput.Builder].
        ///
        /// @param textInput the [TextInput] to build the [TextInputDefinition] from
        /// @return the new [TextInputDefinition]
        public TextInputDefinition with(TextInput.Builder textInput) {
            return new TextInputDefinition(
                    parameter,
                    textInput.getLabel(),
                    textInput.getPlaceholder(),
                    textInput.getValue(),
                    textInput.getMinLength(),
                    textInput.getMaxLength(),
                    textInput.getStyle(),
                    textInput.isRequired()
            );
        }

        @Override
        public @NonNull String displayName() {
            return label;
        }

        /// Transforms this definition into a [TextInput].
        ///
        /// @return the [TextInput]
        @NonNull
        @Override
        public TextInput toJDAEntity() {
            return toBuilder().build();
        }

        /// Transforms this definition into a [TextInput.Builder].
        ///
        /// @return the [TextInput.Builder]
        public TextInput.Builder toBuilder() {
            var textInput = TextInput.create(label, label, style).setRequired(required);

            if (minValue != -1) {
                textInput.setMinLength(minValue);
            }
            if (maxValue != -1) {
                textInput.setMaxLength(maxValue);
            }
            if (placeholder != null && !placeholder.isBlank()) {
                textInput.setPlaceholder(placeholder);
            }
            if (defaultValue != null && !defaultValue.isBlank()) {
                textInput.setValue(defaultValue);
            }
            return textInput;
        }
    }
}
