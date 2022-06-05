package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.annotations.interactions.Button;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Optional;

public class ButtonDefinition {

    private final String id;
    private final String label;
    private final Emoji emoji;
    private final String link;
    private final ButtonStyle style;
    private final Method method;
    private final Object instance;

    private ButtonDefinition(String id, String label, Emoji emoji, String link, ButtonStyle style, Method method, Object instance) {
        this.id = id;
        this.label = label;
        this.emoji = emoji;
        this.link = link;
        this.style = style;
        this.method = method;
        this.instance = instance;
    }

    public static Optional<ButtonDefinition> build(@NotNull Method method, @NotNull Object instance) {
        if (!method.isAnnotationPresent(Button.class) || !method.getDeclaringClass().isAnnotationPresent(CommandController.class)) {
            return Optional.empty();
        }

        Button button = method.getAnnotation(Button.class);

        Emoji emoji;
        String emojiString = button.emoji();
        if (emojiString.matches("<:[\\w-]{2,}:[0-9]{4,}>")) {
            emoji = Emoji.fromMarkdown(emojiString);
        } else if (emojiString.matches(":[\\w-]+:")) {
            emoji = Emoji.fromUnicode(emojiString);
        } else {
            emoji = null;
        }

        String name = button.id().isEmpty() ? method.getName() : button.id();
        name = String.format("%s.%s", method.getDeclaringClass().getSimpleName(), name);
        return Optional.of(new ButtonDefinition(
                name,
                button.label(),
                emoji,
                button.link(),
                button.style(),
                method, instance));

    }

    public net.dv8tion.jda.api.interactions.components.buttons.Button toButton() {
        String label = getLabel().orElse("empty label");
        String id = getLink().orElse(this.id);
        if (emoji == null) {
            return net.dv8tion.jda.api.interactions.components.buttons.Button.of(style, id, label);
        } else {
            return net.dv8tion.jda.api.interactions.components.buttons.Button.of(style, id, label, emoji);
        }
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public Optional<String> getLabel() {
        return Optional.ofNullable(label.isEmpty() ? null : label);
    }

    @NotNull
    public Optional<Emoji> getEmoji() {
        return Optional.ofNullable(emoji);
    }

    @NotNull
    public Optional<String> getLink() {
        return Optional.ofNullable(link.isEmpty() ? null : link);
    }

    @NotNull
    public ButtonStyle getStyle() {
        return style;
    }

    @NotNull
    public Method getMethod() {
        return method;
    }

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
