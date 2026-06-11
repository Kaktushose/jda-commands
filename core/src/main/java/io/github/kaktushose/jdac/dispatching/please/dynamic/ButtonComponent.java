package io.github.kaktushose.jdac.dispatching.please.dynamic;

import io.github.kaktushose.jdac.definitions.interactions.component.ButtonDefinition;
import io.github.kaktushose.jdac.dispatching.please.Component;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.entities.SkuSnowflake;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import org.jspecify.annotations.Nullable;

/// An implementation of [Component] specific to [Button]
public final class ButtonComponent extends Component<ButtonComponent, Button, Button, ButtonDefinition>
        implements Button {

    private @Nullable Emoji emoji;
    private @Nullable ButtonStyle buttonStyle;
    private @Nullable String label;
    private @Nullable String url;

    public ButtonComponent(String method, @Nullable Class<?> origin, Entry[] placeholder) {
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
        return definition.with(label, emoji, url, buttonStyle, uniqueId);
    }

    @Override
    public Type getType() {
        return Type.BUTTON;
    }

    @Override
    public @Nullable String getLabel() {
        return label;
    }

    @Override
    public @Nullable ButtonStyle getStyle() {
        return buttonStyle;
    }

    @Override
    public @Nullable String getUrl() {
        return url;
    }

    @Override
    public @Nullable SkuSnowflake getSku() {
        return null;
    }

    @Override
    public @Nullable EmojiUnion getEmoji() {
        return (EmojiUnion) emoji;
    }

    @Override
    public boolean isDisabled() {
        return !enabled();
    }

    @Override
    public @Nullable String getCustomId() {
        return null;
    }
}
