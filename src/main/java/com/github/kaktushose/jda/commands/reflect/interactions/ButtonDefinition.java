package com.github.kaktushose.jda.commands.reflect.interactions;

import com.github.kaktushose.jda.commands.annotations.interactions.Button;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.buttons.ButtonEvent;
import com.github.kaktushose.jda.commands.dispatching.commands.CommandContext;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Representation of a {@link net.dv8tion.jda.api.interactions.components.buttons.Button Button}.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @see Button
 * @since 2.3.0
 */
public class ButtonDefinition extends EphemeralInteraction {

    private final String label;
    private final Emoji emoji;
    private final String link;
    private final ButtonStyle style;

    protected ButtonDefinition(Method method,
                               boolean ephemeral,
                               String label,
                               Emoji emoji,
                               String link,
                               ButtonStyle style) {
        super(method, ephemeral);
        this.label = label;
        this.emoji = emoji;
        this.link = link;
        this.style = style;
    }

    /**
     * Builds a new ButtonDefinition.
     *
     * @param method the {@link Method} of the button
     * @return an {@link Optional} holding the ButtonDefinition
     */
    public static Optional<ButtonDefinition> build(@NotNull Method method) {
        if (!method.isAnnotationPresent(Button.class) || !method.getDeclaringClass().isAnnotationPresent(Interaction.class)) {
            return Optional.empty();
        }

        if (method.getParameters().length != 1) {
            log.error("An error has occurred! Skipping Button {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException("Invalid amount of parameters!"));
            return Optional.empty();
        }

        if (!ButtonEvent.class.isAssignableFrom(method.getParameters()[0].getType())) {
            log.error("An error has occurred! Skipping Button {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException(String.format("First parameter must be of type %s!", ButtonEvent.class.getSimpleName())));
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
                button.ephemeral(),
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
        String id = getLink().orElse(this.id);
        if (emoji == null) {
            return net.dv8tion.jda.api.interactions.components.buttons.Button.of(style, id, label);
        } else {
            return net.dv8tion.jda.api.interactions.components.buttons.Button.of(style, id, label, emoji);
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

    /**
     * Gets the runtime id. The runtime id is composed of the static interaction id and the
     * snowflake id of the interaction event that created the runtime.
     *
     * @param context the {@link CommandContext} this button will be attached to
     * @return the runtime id
     */
    @NotNull
    public String getRuntimeId(GenericContext<? extends GenericInteractionCreateEvent> context) {
        return String.format("%s.%s", getId(), context.getRuntime().getInstanceId());
    }

    @Override
    public String toString() {
        return "ButtonDefinition{" +
                "label='" + label + '\'' +
                ", emoji=" + emoji +
                ", link='" + link + '\'' +
                ", style=" + style +
                ", ephemeral=" + ephemeral +
                ", id='" + id + '\'' +
                ", method=" + method +
                '}';
    }
}
