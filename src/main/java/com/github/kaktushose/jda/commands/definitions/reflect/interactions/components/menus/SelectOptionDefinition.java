package com.github.kaktushose.jda.commands.definitions.reflect.interactions.components.menus;

import com.github.kaktushose.jda.commands.annotations.interactions.SelectOption;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import javax.annotation.Nullable;

/**
 * Representation of a {@link net.dv8tion.jda.api.interactions.components.selections.SelectOption SelectOption}.
 *
 * @see SelectOption
 * @since 4.0.0
 */
public record SelectOptionDefinition(
        String value,
        String label,
        String description,
        @Nullable Emoji emoji,
        boolean isDefault
) {

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
}
