package com.github.kaktushose.jda.commands.definitions.interactions.impl;

import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.features.CustomIdJDAEntity;
import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.interactions.CustomId;
import com.github.kaktushose.jda.commands.definitions.interactions.Interaction;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ModalEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SequencedCollection;


public record ModalDefinition(
        ClassDescription clazz,
        MethodDescription method,
        SequencedCollection<Class<?>> parameters,
        Collection<String> permissions,
        String title,
        SequencedCollection<TextInputDefinition> textInputs
) implements Interaction, CustomIdJDAEntity<Modal> {

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

    @NotNull
    @Override
    public SequencedCollection<Class<?>> methodSignature() {
        List<Class<?>> parameters = new ArrayList<>();
        parameters.add(ModalEvent.class);
        textInputs.forEach(_ -> parameters.add(String.class));
        return parameters;
    }


    public record TextInputDefinition(
            String label,
            String placeholder,
            String defaultValue,
            int minValue,
            int maxValue,
            TextInputStyle style,
            boolean required
    ) implements JDAEntity<TextInput>, Definition {
        @Override
        public @NotNull String displayName() {
            return label;
        }

        @Override
        public @NotNull TextInput toJDAEntity() {
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
