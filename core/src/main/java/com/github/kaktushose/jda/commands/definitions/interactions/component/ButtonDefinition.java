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
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.github.kaktushose.jda.commands.definitions.interactions.component.ComponentDefinition.override;

/// Representation of a button.
///
/// @param classDescription  the [ClassDescription] of the declaring class of the [#methodDescription()]
/// @param methodDescription the [MethodDescription] of the method this definition is bound to
/// @param permissions       a [Collection] of permissions for this button
/// @param label             the label of this button
/// @param emoji             the [Emoji] of this button or `null`
/// @param link              the link of this button or `null`
/// @param style             the [ButtonStyle] of this button
public record ButtonDefinition(
        ClassDescription classDescription,
        MethodDescription methodDescription,
        Collection<String> permissions,
        String label,
        @Nullable Emoji emoji,
        @Nullable String link,
        ButtonStyle style
) implements ComponentDefinition<Button> {

    /// Constructs a new [ButtonDefinition] from the given [MethodBuildContext].
    ///
    /// @return an [ButtonDefinition] holding the [StringSelectMenuDefinition]
    public static ButtonDefinition build(MethodBuildContext context) {
        var method = context.method();
        com.github.kaktushose.jda.commands.annotations.interactions.Button button =
                method.annotation(com.github.kaktushose.jda.commands.annotations.interactions.Button.class).orElseThrow();

        Helpers.checkSignature(method, List.of(ComponentEvent.class));

        Emoji emoji;
        String emojiString = button.emoji();
        if (emojiString.isEmpty()) {
            emoji = null;
        } else {
            emoji = Emoji.fromFormatted(emojiString);
        }

        return new ButtonDefinition(
                context.clazz(),
                method,
                Helpers.permissions(context),
                button.value(),
                emoji,
                button.link().isEmpty() ? null : button.link(),
                button.style()
        );
    }

    /// Builds a new [ButtonDefinition] with the given values

    public ButtonDefinition with(
            @Nullable String label, @Nullable Emoji emoji, @Nullable String link, @Nullable ButtonStyle style) {
        return new ButtonDefinition(classDescription, methodDescription, permissions,
                override(this.label, label), override(this.emoji, emoji), override(this.link, link), override(this.style, style));
    }

    /// Transforms this definition to an [Button] with an independent custom id.
    ///
    /// @return the [Button]
    /// @see CustomId#independent(String)
    @Override
    public Button toJDAEntity() {
        return toJDAEntity(CustomId.independent(definitionId()));
    }

    /// Transforms this definition to an [Button] with the given [CustomId].
    ///
    /// @param customId the [CustomId] to use
    /// @return the [Button]
    @Override
    public Button toJDAEntity(CustomId customId) {
        try {
            String idOrUrl = Optional.ofNullable(link).orElse(customId.merged());
            if (emoji == null) {
                return Button.of(style, idOrUrl, label);
            } else {
                return Button.of(style, idOrUrl, label, emoji);
            }
        } catch (IllegalArgumentException e) {
            throw Helpers.jdaException(e, this);
        }
    }

    @Override
    public String displayName() {
        return "Button: %s".formatted(label.isEmpty() ? definitionId() : label);
    }
}
