package com.github.kaktushose.jda.commands.definitions.interactions.impl;

import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.interactions.Interaction;
import com.github.kaktushose.jda.commands.definitions.interactions.MethodBuildContext;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public record ButtonDefinition(
        @NotNull ClassDescription clazz,
        @NotNull MethodDescription method,
        @NotNull Collection<String> permissions,
        @NotNull String label,
        @Nullable Emoji emoji,
        @Nullable String link,
        @NotNull ButtonStyle style
) implements Interaction, JDAEntity<Button> {

    public static Optional<Definition> build(MethodBuildContext context) {
        var method = context.method();
        com.github.kaktushose.jda.commands.annotations.interactions.Button button =
                method.annotation(com.github.kaktushose.jda.commands.annotations.interactions.Button.class).orElseThrow();

        if (Helpers.checkSignature(method, List.of(ComponentEvent.class))) {
            return Optional.empty();
        }

        Emoji emoji;
        String emojiString = button.emoji();
        if (emojiString.isEmpty()) {
            emoji = null;
        } else {
            emoji = Emoji.fromFormatted(emojiString);
        }

        return Optional.of(new ButtonDefinition(
                context.clazz(),
                method,
                Helpers.permissions(context),
                button.value(),
                emoji,
                button.link(),
                button.style()
        ));
    }

    @NotNull
    @Override
    public Button toJDAEntity() {
        String idOrUrl = Optional.ofNullable(link).orElse(definitionId());
        if (emoji == null) {
            return Button.of(style, idOrUrl, label);
        } else {
            return Button.of(style, idOrUrl, label, emoji);
        }
    }

    @NotNull
    @Override
    public String displayName() {
        return label.isEmpty() ? definitionId() : label;
    }

}
