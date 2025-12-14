package io.github.kaktushose.jdac.definitions.interactions.component;

import io.github.kaktushose.jdac.definitions.description.ClassDescription;
import io.github.kaktushose.jdac.definitions.description.MethodDescription;
import io.github.kaktushose.jdac.definitions.interactions.CustomId;
import io.github.kaktushose.jdac.definitions.interactions.MethodBuildContext;
import io.github.kaktushose.jdac.definitions.interactions.component.menu.StringSelectMenuDefinition;
import io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent;
import io.github.kaktushose.jdac.internal.Helpers;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static io.github.kaktushose.jdac.definitions.interactions.component.ComponentDefinition.override;

/// Representation of a button.
///
/// @param classDescription  the [ClassDescription] of the declaring class of the [#methodDescription()]
/// @param methodDescription the [MethodDescription] of the method this definition is bound to
/// @param permissions       a [Collection] of permissions for this button
/// @param label             the label of this button
/// @param emoji             the [Emoji] of this button or `null`
/// @param link              the link of this button or `null`
/// @param style             the [ButtonStyle] of this button
/// @param uniqueId          the uniqueId of this button
public record ButtonDefinition(
        ClassDescription classDescription,
        MethodDescription methodDescription,
        Collection<String> permissions,
        String label,
        @Nullable Emoji emoji,
        @Nullable String link,
        ButtonStyle style,
        @Nullable Integer uniqueId
) implements ComponentDefinition<Button> {

    /// Constructs a new [ButtonDefinition] from the given [MethodBuildContext].
    ///
    /// @return an [ButtonDefinition] holding the [StringSelectMenuDefinition]
    public static ButtonDefinition build(MethodBuildContext context) {
        var method = context.method();
        io.github.kaktushose.jdac.annotations.interactions.Button button =
                method.annotation(io.github.kaktushose.jdac.annotations.interactions.Button.class).orElseThrow();

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
                button.style(),
                button.uniqueId() < 0 ? null : button.uniqueId()
        );
    }

    /// Builds a new [ButtonDefinition] with the given values

    public ButtonDefinition with(@Nullable String label,
                                 @Nullable Emoji emoji,
                                 @Nullable String link,
                                 @Nullable ButtonStyle style,
                                 @Nullable Integer uniqueId) {
        return new ButtonDefinition(
                classDescription,
                methodDescription,
                permissions,
                override(this.label, label),
                override(this.emoji, emoji),
                override(this.link, link),
                override(this.style, style),
                override(this.uniqueId, uniqueId));
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
            Button button;
            if (emoji == null) {
                button = Button.of(style, idOrUrl, label);
            } else {
                button = Button.of(style, idOrUrl, label, emoji);
            }
            if (uniqueId != null) {
                button = button.withUniqueId(uniqueId);
            }
            return button;
        } catch (IllegalArgumentException e) {
            throw Helpers.jdaException(e, this);
        }
    }

    @Override
    public String displayName() {
        return "Button: %s".formatted(label.isEmpty() ? definitionId() : label);
    }
}
