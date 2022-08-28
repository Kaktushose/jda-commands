package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.annotations.interactions.Button;
import com.github.kaktushose.jda.commands.dispatching.ButtonEvent;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Representation of a {@link net.dv8tion.jda.api.interactions.components.buttons.Button Button}.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see Button
 * @since 2.3.0
 */
public class ButtonDefinition {

    private static final Logger log = LoggerFactory.getLogger(ButtonDefinition.class);
    private final String id;
    private final String label;
    private final Emoji emoji;
    private final String link;
    private final ButtonStyle style;
    private final Method method;
    private final Object instance;
    private boolean isEphemeral;
    private ControllerDefinition controller;

    private ButtonDefinition(String id,
                             String label,
                             Emoji emoji,
                             String link,
                             ButtonStyle style,
                             boolean isEphemeral,
                             Method method,
                             Object instance) {
        this.id = id;
        this.label = label;
        this.emoji = emoji;
        this.link = link;
        this.style = style;
        this.isEphemeral = isEphemeral;
        this.method = method;
        this.instance = instance;
    }

    /**
     * Builds a new ButtonDefinition.
     *
     * @param method   the {@link Method} of the button
     * @param instance an instance of the method defining class
     * @return an {@link Optional} holding the ButtonDefinition
     */
    public static Optional<ButtonDefinition> build(@NotNull Method method, @NotNull Object instance) {
        if (!method.isAnnotationPresent(Button.class) || !method.getDeclaringClass().isAnnotationPresent(CommandController.class)) {
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

        String name = button.id().isEmpty() ? method.getName() : button.id();
        name = String.format("%s.%s", method.getDeclaringClass().getSimpleName(), name);
        return Optional.of(new ButtonDefinition(
                name,
                button.label(),
                emoji,
                button.link(),
                button.style(),
                button.ephemeral(),
                method,
                instance)
        );
    }

    /**
     * Transforms this command definition to a {@link net.dv8tion.jda.api.interactions.components.buttons.Button Button}.
     *
     * @return the transformed {@link net.dv8tion.jda.api.interactions.components.buttons.Button Button}
     */
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
     * Gets the id of the button.
     *
     * @return the id of the button
     */
    @NotNull
    public String getId() {
        return id;
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
     * Whether this button should send ephemeral replies by default.
     *
     * @return {@code true} if to send ephemeral replies
     */
    public boolean isEphemeral() {
        return isEphemeral;
    }

    /**
     * Set whether this button should send ephemeral replies by default.
     *
     * @param ephemeral whether to send ephemeral replies
     */
    public void setEphemeral(boolean ephemeral) {
        isEphemeral = ephemeral;
    }

    /**
     * Gets the {@link ControllerDefinition} this button is defined inside. Can be null during indexing.
     *
     * @return the {@link ControllerDefinition}
     */
    @Nullable
    public ControllerDefinition getController() {
        return controller;
    }

    /**
     * Sets the {@link ControllerDefinition}.
     *
     * @param controller the {@link ControllerDefinition} to use
     */
    public void setController(@NotNull ControllerDefinition controller) {
        this.controller = controller;
    }

    /**
     * Gets the {@link Method} of the command.
     *
     * @return the {@link Method} of the command
     */
    @NotNull
    public Method getMethod() {
        return method;
    }

    /**
     * Gets an instance of the method defining class
     *
     * @return an instance of the method defining class
     */
    @NotNull
    public Object getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return "ButtonDefinition{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", emoji=" + emoji +
                ", link='" + link + '\'' +
                ", style=" + style +
                '}';
    }
}
