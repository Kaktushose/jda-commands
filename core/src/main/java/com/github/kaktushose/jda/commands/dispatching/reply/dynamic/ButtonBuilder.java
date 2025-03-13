package com.github.kaktushose.jda.commands.dispatching.reply.dynamic;

import com.github.kaktushose.jda.commands.definitions.interactions.component.ButtonDefinition;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;

public final class ButtonBuilder implements ComponentBuilder {

    private final ButtonDefinition definition;
    private Button button;

    public ButtonBuilder(ButtonDefinition definition) {
        this.definition = definition;
        this.button = definition.toJDAEntity();
    }

    public ButtonBuilder enabled() {
        button = button.withDisabled(false);
        return this;
    }

    public ButtonBuilder disabled() {
        button = button.withDisabled(true);
        return this;
    }

    public ButtonBuilder emoji(@NotNull Emoji emoji) {
        button = button.withEmoji(emoji);
        return this;
    }

    public ButtonBuilder style(@NotNull ButtonStyle style) {
        button = button.withStyle(style);
        return this;
    }

    public ButtonBuilder label(@NotNull String label) {
        button = button.withLabel(label);
        return this;
    }

    public ButtonBuilder url(@NotNull String url) {
        button = button.withUrl(url);
        return this;
    }

    @Override
    public ButtonDefinition build() {
        return new ButtonDefinition(definition, button);
    }
}
