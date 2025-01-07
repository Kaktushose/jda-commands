package com.github.kaktushose.jda.commands.definitions.interactions.component;

import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.interactions.CustomId;
import com.github.kaktushose.jda.commands.definitions.interactions.MethodBuildContext;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.StringSelectMenuDefinition;
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

/// Representation of a button.
///
/// @param clazzDescription  the [ClassDescription] of the declaring class of the [#methodDescription()]
/// @param methodDescription the [MethodDescription] of the method this definition is bound to
/// @param permissions       a [Collection] of permissions for this button
/// @param label             the label of this button
/// @param emoji             the [Emoji] of this button or `null`
/// @param link              the link of this button or `null`
/// @param style             the [ButtonStyle] of this button
public record ButtonDefinition(
        @NotNull ClassDescription clazzDescription,
        @NotNull MethodDescription methodDescription,
        @NotNull Collection<String> permissions,
        @NotNull String label,
        @Nullable Emoji emoji,
        @Nullable String link,
        @NotNull ButtonStyle style
) implements ComponentDefinition<Button> {

    /// Constructs a new [ButtonDefinition] from the given [MethodBuildContext].
    ///
    /// @return an [ButtonDefinition] holding the [StringSelectMenuDefinition]
    public static Optional<ButtonDefinition> build(MethodBuildContext context) {
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
                button.link().isEmpty() ? null : button.link(),
                button.style()
        ));
    }

    /// Transforms this definition to an [Button] with an independent custom id.
    ///
    /// @return the [Button]
    /// @see CustomId#independent(String)
    @NotNull
    @Override
    public Button toJDAEntity() {
        return toJDAEntity(CustomId.independent(definitionId()));
    }

    /// Transforms this definition to an [Button] with the given [CustomId].
    ///
    /// @param customId the [CustomId] to use
    /// @return the [Button]
    @NotNull
    @Override
    public Button toJDAEntity(@NotNull CustomId customId) {
        String idOrUrl = Optional.ofNullable(link).orElse(customId.id());
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
