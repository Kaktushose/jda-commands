package com.github.kaktushose.jda.commands.reflect.interactions.components.menus;

import com.github.kaktushose.jda.commands.annotations.interactions.SelectOption;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.Optional;

/**
 * Representation of a {@link net.dv8tion.jda.api.interactions.components.selections.SelectOption SelectOption}.
 *
 * @see SelectOption
 * @since 4.0.0
 */
public class SelectOptionDefinition {

    private final String value;
    private final String label;
    private final String description;
    private final Emoji emoji;
    private final boolean isDefault;

    protected SelectOptionDefinition(String value, String label, String description, Emoji emoji, boolean isDefault) {
        this.value = value;
        this.label = label;
        this.description = description;
        this.emoji = emoji;
        this.isDefault = isDefault;
    }

    /**
     * Builds a new SelectOptionDefinition.
     *
     * @param option the {@link SelectOption} to build the SelectOptionDefinition from
     * @return the SelectOptionDefinition
     */
    public static SelectOptionDefinition build(SelectOption option) {
        Emoji emoji;
        String emojiString = option.emoji();
        if (emojiString.isEmpty()) {
            emoji = null;
        } else {
            emoji = Emoji.fromFormatted(emojiString);
        }
        return new SelectOptionDefinition(option.value(), option.label(), option.description(), emoji, option.isDefault());
    }

    public net.dv8tion.jda.api.interactions.components.selections.SelectOption toSelectOption() {
        return net.dv8tion.jda.api.interactions.components.selections.SelectOption.of(label, value)
                .withDescription(description)
                .withEmoji(emoji);
    }

    /**
     * Gets the value of the SelectOption.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the label of the SelectOption.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets the description of the SelectOption.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the {@link Emoji} of the SelectOption.
     *
     * @return an {@link Optional} holding the {@link Emoji}
     */
    public Optional<Emoji> getEmoji() {
        return Optional.ofNullable(emoji);
    }

    /**
     * Gets whether this SelectOption is a default option.
     *
     * @return {@code true} if this SelectOption is a default option
     */
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public String toString() {
        return "SelectOptionDefinition{" +
                "value='" + value + '\'' +
                ", label='" + label + '\'' +
                ", description='" + description + '\'' +
                ", emoji=" + emoji +
                ", isDefault=" + isDefault +
                '}';
    }
}
