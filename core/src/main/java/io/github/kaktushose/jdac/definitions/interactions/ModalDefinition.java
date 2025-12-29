package io.github.kaktushose.jdac.definitions.interactions;

import io.github.kaktushose.jdac.definitions.Definition;
import io.github.kaktushose.jdac.definitions.description.ClassDescription;
import io.github.kaktushose.jdac.definitions.description.MethodDescription;
import io.github.kaktushose.jdac.definitions.description.ParameterDescription;
import io.github.kaktushose.jdac.definitions.features.CustomIdJDAEntity;
import io.github.kaktushose.jdac.definitions.features.JDAEntity;
import io.github.kaktushose.jdac.definitions.interactions.component.ComponentDefinition;
import io.github.kaktushose.jdac.dispatching.events.interactions.ModalEvent;
import io.github.kaktushose.jdac.internal.Helpers;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInput.Builder;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.modals.Modal;
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
        ClassDescription classDescription,
        MethodDescription methodDescription,
        Collection<String> permissions,
        String title,
        SequencedCollection<TextInputDefinition> textInputs
) implements InteractionDefinition, CustomIdJDAEntity<Modal> {

    /// Builds a new [ModalDefinition] from the given [MethodBuildContext].
    ///
    /// @return an [Optional] holding the [ModalDefinition]
    public static ModalDefinition build(MethodBuildContext context) {
        var method = context.method();
        var modal = method.annotation(io.github.kaktushose.jdac.annotations.interactions.Modal.class).orElseThrow();

        Helpers.checkParameterType(method, 0, ModalEvent.class);

        List<TextInputDefinition> textInputs = new ArrayList<>();
        for (int i = 1; i < method.parameters().size(); i++) {
            var parameter = List.copyOf(method.parameters()).get(i);
            TextInputDefinition.build(parameter).ifPresent(textInputs::add);
        }

        List<Class<?>> signature = new ArrayList<>();
        signature.add(ModalEvent.class);
        textInputs.forEach(_ -> signature.add(String.class));

        Helpers.checkSignature(method, signature);


        return new ModalDefinition(context.clazz(), method, Helpers.permissions(context), modal.value(), textInputs);
    }

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

    /// Transforms this definition to an [Modal] with the given custom id.
    ///
    /// @return the [Modal]
    /// @see CustomId#independent(String)
    public Modal toJDAEntity(CustomId customId) {
        try {
            var modal = Modal.create(customId.merged(), title);

            textInputs.forEach(textInput -> modal.addComponents(textInput.toJDAEntity(0)));

            return modal.build();
        } catch (IllegalArgumentException e) {
            throw Helpers.jdaException(e, this);
        }
    }

    @Override
    public String displayName() {
        return "Modal: %s".formatted(title);
    }

    /// Representation of a modal text input defined by
    /// [`TextInput`][io.github.kaktushose.jdac.annotations.interactions.TextInput]
    public record TextInputDefinition(
            ParameterDescription parameter,
            String label,
            @Nullable String placeholder,
            @Nullable String defaultValue,
            int minValue,
            int maxValue,
            TextInputStyle style,
            boolean required
    ) implements JDAEntity<Label>, Definition {

        /// Builds a new [TextInputDefinition] from the given [ParameterDescription]
        ///
        /// @param parameter the [ParameterDescription] to build the [TextInputDefinition] from
        /// @return the new [TextInputDefinition]
        public static Optional<TextInputDefinition> build(ParameterDescription parameter) {
            var optional = parameter.annotation(io.github.kaktushose.jdac.annotations.interactions.TextInput.class);

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
        public TextInputDefinition with(Builder textInput) {
            return new TextInputDefinition(
                    parameter,
                    label,
                    textInput.getPlaceholder(),
                    textInput.getValue(),
                    textInput.getMinLength(),
                    textInput.getMaxLength(),
                    textInput.getStyle(),
                    textInput.isRequired()
            );
        }

        @Override
        public String displayName() {
            return label;
        }

        /// Transforms this definition into a [TextInput].
        ///
        /// @return the [TextInput]
        @Override
        public Label toJDAEntity(int counter) {
            return Label.of(label, toBuilder().build());
        }

        /// Transforms this definition into a [TextInput.Builder].
        ///
        /// @return the [TextInput.Builder]
        public Builder toBuilder() {
            var textInput = TextInput.create(label, style).setRequired(required);

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
