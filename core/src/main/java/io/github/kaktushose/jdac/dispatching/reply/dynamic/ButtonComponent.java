package io.github.kaktushose.jdac.dispatching.reply.dynamic;

import io.github.kaktushose.jdac.definitions.interactions.component.ButtonDefinition;
import io.github.kaktushose.jdac.dispatching.reply.Component;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.components.section.SectionAccessoryComponentUnion;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.jspecify.annotations.Nullable;

/// An implementation of [Component] specific to [Button]
public final class ButtonComponent extends Component<ButtonComponent, Button, Button, ButtonDefinition>
        implements SectionAccessoryComponentUnion {

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
    public Type getType() {
        return Type.BUTTON;
    }

    @Override
    public Thumbnail asThumbnail() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Class<ButtonDefinition> definitionClass() {
        return ButtonDefinition.class;
    }

    @Override
    protected ButtonDefinition build(ButtonDefinition definition) {
        return definition.with(label, emoji, url, buttonStyle, uniqueId);
    }
}
