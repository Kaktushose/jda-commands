package com.github.kaktushose.jda.commands.dispatching.reply.component;

import com.github.kaktushose.jda.commands.definitions.interactions.component.ButtonDefinition;
import com.github.kaktushose.jda.commands.dispatching.reply.Component;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;

public final class ButtonComponent extends Component<ButtonComponent, ButtonDefinition> {

    private Emoji emoji;
    private ButtonStyle buttonStyle;
    private String label;
    private String url;

    public ButtonComponent emoji(@NotNull Emoji emoji) {
        this.emoji = emoji;
        return this;
    }

    public ButtonComponent style(@NotNull ButtonStyle style) {
        this.buttonStyle = style;
        return this;
    }

    public ButtonComponent label(@NotNull String label) {
        this.label = label;
        return this;
    }

    public ButtonComponent url(@NotNull String url) {
        this.url = url;
        return this;
    }

    @Override
    public ButtonDefinition build(ButtonDefinition definition) {
        return definition.with(label, emoji, url, buttonStyle);
    }
}
