package com.github.kaktushose.jda.commands.definitions.reflect.interactions.components;

import com.github.kaktushose.jda.commands.internal.Helpers;
import com.github.kaktushose.jda.commands.annotations.interactions.Button;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.definitions.reflect.MethodBuildContext;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.ReplyConfig;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

/**
 * Representation of a {@link net.dv8tion.jda.api.interactions.components.buttons.Button Button}.
 *
 * @see Button
 * @since 2.3.0
 */
public final class ButtonDefinition extends GenericComponentDefinition {

    private final String label;
    private final Emoji emoji;
    private final String link;
    private final ButtonStyle style;

    private ButtonDefinition(Method method,
                             Set<String> permissions,
                             ReplyConfig replyConfig,
                             String label,
                             Emoji emoji,
                             String link,
                             ButtonStyle style) {
        super(method, permissions, replyConfig);
        this.label = label;
        this.emoji = emoji;
        this.link = link;
        this.style = style;
    }

    /**
     * Builds a new ButtonDefinition.
     *
     * @return an {@link Optional} holding the ButtonDefinition
     */
    public static Optional<ButtonDefinition> build(MethodBuildContext context) {
        Method method = context.method();
        if (!method.isAnnotationPresent(Button.class) || !method.getDeclaringClass().isAnnotationPresent(Interaction.class)) {
            return Optional.empty();
        }

        if (Helpers.isIncorrectParameterAmount(method, 1)) {
            return Optional.empty();
        }

        if (Helpers.isIncorrectParameterType(method, 0, ComponentEvent.class)) {
            return Optional.empty();
        }

        Button button = method.getAnnotation(Button.class);
        Emoji emoji;
        String emojiString = button.emoji();
        if (emojiString.isEmpty()) {
            emoji = null;
        } else {
            emoji = Emoji.fromFormatted(emojiString);
        }

        return Optional.of(new ButtonDefinition(
                method,
                Helpers.permissions(context),
                Helpers.replyConfig(method),
                button.value(),
                emoji,
                button.link(),
                button.style()
        ));
    }

    /**
     * Transforms this command definition to a {@link net.dv8tion.jda.api.interactions.components.buttons.Button Button}.
     *
     * @return the transformed {@link net.dv8tion.jda.api.interactions.components.buttons.Button Button}
     */
    @NotNull
    public net.dv8tion.jda.api.interactions.components.buttons.Button toButton() {
        String label = getLabel().orElse("");
        String idOrUrl = getLink().orElse(this.definitionId);
        if (emoji == null) {
            return net.dv8tion.jda.api.interactions.components.buttons.Button.of(style, idOrUrl, label);
        } else {
            return net.dv8tion.jda.api.interactions.components.buttons.Button.of(style, idOrUrl, label, emoji);
        }
    }

    /**
     * Gets the label of the button.
     *
     * @return an {@link Optional} holding the label
     */
    @NotNull
    public Optional<String> getLabel() {
        return Optional.ofNullable(label.isEmpty() ? null : label);
    }

    /**
     * Gets the {@link Emoji} of the button.
     *
     * @return an {@link Optional} holding the {@link Emoji}
     */
    @NotNull
    public Optional<Emoji> getEmoji() {
        return Optional.ofNullable(emoji);
    }

    /**
     * Gets the link of the button.
     *
     * @return an {@link Optional} holding the link
     */
    @NotNull
    public Optional<String> getLink() {
        return Optional.ofNullable(link.isEmpty() ? null : link);
    }

    /**
     * Gets the {@link ButtonStyle}.
     *
     * @return the {@link ButtonStyle}
     */
    @NotNull
    public ButtonStyle getStyle() {
        return style;
    }

    @Override
    public String getDisplayName() {
        return getLabel().orElse(definitionId);
    }

    @Override
    public String toString() {
        return "ButtonDefinition{" +
                "label='" + label + '\'' +
                ", emoji=" + emoji +
                ", link='" + link + '\'' +
                ", style=" + style +
                ", replyConfig=" + replyConfig +
                ", permissions=" + permissions +
                ", id='" + definitionId + '\'' +
                ", method=" + method +
                '}';
    }
}
