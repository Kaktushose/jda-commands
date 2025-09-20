package com.github.kaktushose.jda.commands.dispatching.reply.dynamic;

import com.github.kaktushose.jda.commands.definitions.interactions.component.ButtonDefinition;
import com.github.kaktushose.jda.commands.dispatching.reply.Component;
import com.github.kaktushose.jda.commands.message.i18n.I18n;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jspecify.annotations.Nullable;

/// An implementation of [Component] specific to [Button]
public final class ButtonComponent extends Component<ButtonComponent, Button, Button, ButtonDefinition> {

    private @Nullable Emoji emoji;
    private @Nullable ButtonStyle buttonStyle;
    private @Nullable String label;
    private @Nullable String url;

    public ButtonComponent(String method, @Nullable Class<?> origin, I18n.Entry[] placeholder) {
        super(method, origin, placeholder);
    }

    /// @see Button#withEmoji(Emoji)
    public ButtonComponent emoji(@Nullable Emoji emoji) {
        this.emoji = emoji;
        return this;
    }

    /// @see Button#withStyle(ButtonStyle)
    public ButtonComponent style(ButtonStyle style) {
        this.buttonStyle = style;
        return this;
    }

    /// @see Button#withLabel(String)
    public ButtonComponent label(String label) {
        this.label = label;
        return this;
    }

    /// @see Button#withUrl(String)
    public ButtonComponent url(String url) {
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
