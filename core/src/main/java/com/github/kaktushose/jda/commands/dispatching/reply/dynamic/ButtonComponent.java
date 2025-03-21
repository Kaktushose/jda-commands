package com.github.kaktushose.jda.commands.dispatching.reply.dynamic;

import com.github.kaktushose.jda.commands.definitions.interactions.component.ButtonDefinition;
import com.github.kaktushose.jda.commands.dispatching.reply.Component;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/// An implementation of [Component] specific to [Button]
public final class ButtonComponent extends Component<ButtonComponent, Button, ButtonDefinition> {

    private Emoji emoji;
    private ButtonStyle buttonStyle;
    private String label;
    private String url;

    public ButtonComponent(@NotNull String method, @Nullable Class<?> origin) {
        super(method, origin);
    }

    /// @see Button#withEmoji(Emoji) 
    public ButtonComponent emoji(@Nullable Emoji emoji) {
        this.emoji = emoji;
        return this;
    }

    /// @see Button#withStyle(ButtonStyle)
    public ButtonComponent style(@NotNull ButtonStyle style) {
        this.buttonStyle = style;
        return this;
    }

    /// @see Button#withLabel(String)
    public ButtonComponent label(@NotNull String label) {
        this.label = label;
        return this;
    }

    /// @see Button#withUrl(String)
    public ButtonComponent url(@NotNull String url) {
        this.url = url;
        return this;
    }

    @Override
    protected Class<ButtonDefinition> definitionClass() {
        return ButtonDefinition.class;
    }

    @Override
    protected ButtonDefinition build(ButtonDefinition definition) {
        return definition.with(label, emoji, url, buttonStyle);
    }
}
