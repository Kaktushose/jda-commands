package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.interactions.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInput.Builder;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Parameter;
import java.util.Optional;

/**
 * Representation of a {@link TextInput} of a
 * {@link com.github.kaktushose.jda.commands.annotations.interactions.Modal Modal}.
 *
 * @see TextInput
 * @see com.github.kaktushose.jda.commands.annotations.interactions.Modal Modal
 * @since 4.0.0
 */
public class TextInputDefinition {

    private final String label;
    private final String placeholder;
    private final String defaultValue;
    private final int minValue;
    private final int maxValue;
    private final TextInputStyle style;
    private final boolean required;

    private TextInputDefinition(String label,
                                String placeholder,
                                String defaultValue,
                                int minValue,
                                int maxValue,
                                TextInputStyle style,
                                boolean required) {
        this.label = label;
        this.placeholder = placeholder;
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.style = style;
        this.required = required;
    }

    /**
     * Builds a new TextInputDefinition.
     *
     * @param parameter the {@link Parameter} of the text input
     * @return an {@link Optional} holding the TextInputDefinition
     */
    public static Optional<TextInputDefinition> build(@NotNull Parameter parameter) {
        if (!parameter.isAnnotationPresent(TextInput.class)) {
            return Optional.empty();
        }

        // TODO for now we only allow Strings, maybe add type adapting in the future
        if (!String.class.isAssignableFrom(parameter.getType())) {
            return Optional.empty();
        }

        TextInput textInput = parameter.getAnnotation(TextInput.class);

        return Optional.of(new TextInputDefinition(
                textInput.label().isEmpty() ? parameter.getName() : textInput.label(),
                textInput.value(),
                textInput.defaultValue(),
                textInput.minValue(),
                textInput.maxValue(),
                textInput.style(),
                textInput.required()
        ));
    }

    /**
     * Transforms this TextInputDefinition to a {@link net.dv8tion.jda.api.interactions.components.text.TextInput TextInput}.
     *
     * @return the transformed {@link net.dv8tion.jda.api.interactions.components.text.TextInput TextInput}
     */
    public net.dv8tion.jda.api.interactions.components.text.TextInput toTextInput() {
        Builder textInput = net.dv8tion.jda.api.interactions.components.text.TextInput.create(label, label, style).setRequired(required);

        if (minValue != -1) {
            textInput.setMinLength(minValue);
        }
        if (maxValue != -1) {
            textInput.setMaxLength(maxValue);
        }
        if (!placeholder.isBlank()) {
            textInput.setPlaceholder(placeholder);
        }
        if (!placeholder.isBlank()) {
            textInput.setValue(defaultValue);
        }

        return textInput.build();
    }

    /**
     * Gets the label of the TextInput.
     *
     * @return the label of the TextInput
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets the placeholder of the TextInput.
     *
     * @return the placeholder of the TextInput
     */
    public String getPlaceholder() {
        return placeholder;
    }

    /**
     * Gets the minimum value of the TextInput.
     *
     * @return the minimum value of the TextInput
     */
    public int getMinValue() {
        return minValue;
    }

    /**
     * Gets the maximum value of the TextInput.
     *
     * @return the maximum value of the TextInput
     */
    public int getMaxValue() {
        return maxValue;
    }

    /**
     * Gets the {@link TextInputStyle} of the TextInput.
     *
     * @return the {@link TextInputStyle} of the TextInput
     */
    public TextInputStyle getStyle() {
        return style;
    }

    /**
     * Whether this TextInput is required.
     *
     * @return {@code true} if this TextInput is required
     */
    public boolean isRequired() {
        return required;
    }

    @Override
    public String toString() {
        return "TextInputDefinition{" +
                "label='" + label + '\'' +
                ", placeholder='" + placeholder + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", minValue=" + minValue +
                ", maxValue=" + maxValue +
                ", style=" + style +
                ", required=" + required +
                '}';
    }
}
