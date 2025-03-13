package com.github.kaktushose.jda.commands.definitions.interactions.component.menu;

import com.github.kaktushose.jda.commands.annotations.internal.SelectOptionContainer;
import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.interactions.CustomId;
import com.github.kaktushose.jda.commands.definitions.interactions.MethodBuildContext;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/// Representation of a string select menu.
///
/// @param classDescription  the [ClassDescription] of the declaring class of the [#methodDescription()]
/// @param methodDescription the [MethodDescription] of the method this definition is bound to
/// @param permissions       a [Collection] of permissions for this menu
/// @param selectOptions     the [SelectOptions][SelectOptionDefinition] of this menu
/// @param placeholder       the placeholder text of this menu
/// @param minValue          the minimum amount of choices
/// @param maxValue          the maximum amount of choices
public record StringSelectMenuDefinition(
        @NotNull ClassDescription classDescription,
        @NotNull MethodDescription methodDescription,
        @NotNull Collection<String> permissions,
        @NotNull Set<SelectOptionDefinition> selectOptions,
        @NotNull String placeholder,
        int minValue,
        int maxValue
) implements SelectMenuDefinition<StringSelectMenu> {

    public StringSelectMenuDefinition(@NotNull StringSelectMenuDefinition definition, @NotNull StringSelectMenu menu) {
        this(
                definition.classDescription,
                definition.methodDescription,
                definition.permissions,
                menu.getOptions().stream().map(SelectOptionDefinition::new).collect(Collectors.toSet()),
                Objects.requireNonNull(menu.getPlaceholder()),
                menu.getMinValues(),
                menu.getMaxValues()
        );
    }

    /// Builds a new [StringSelectMenuDefinition] from the given [MethodBuildContext].
    ///
    /// @return an [Optional] holding the [StringSelectMenuDefinition]
    public static Optional<StringSelectMenuDefinition> build(MethodBuildContext context) {
        var method = context.method();
        com.github.kaktushose.jda.commands.annotations.interactions.StringSelectMenu selectMenu =
                method.annotation(com.github.kaktushose.jda.commands.annotations.interactions.StringSelectMenu.class).orElseThrow();

        if (Helpers.checkSignature(method, List.of(ComponentEvent.class, List.class))) {
            return Optional.empty();
        }

        Set<SelectOptionDefinition> selectOptions = new HashSet<>();

        method.annotations().stream()
                .filter(com.github.kaktushose.jda.commands.annotations.interactions.SelectOption.class::isInstance)
                .map(com.github.kaktushose.jda.commands.annotations.interactions.SelectOption.class::cast)
                .forEach(it -> selectOptions.add(SelectOptionDefinition.build(it)));

        method.annotations().stream()
                .filter(SelectOptionContainer.class::isInstance)
                .map(SelectOptionContainer.class::cast)
                .flatMap(it -> Arrays.stream(it.value()))
                .forEach(it -> selectOptions.add(SelectOptionDefinition.build(it)));

        return Optional.of(new StringSelectMenuDefinition(
                context.clazz(),
                method,
                Helpers.permissions(context),
                selectOptions,
                selectMenu.value(),
                selectMenu.minValue(),
                selectMenu.maxValue()
        ));
    }

    /// Transforms this definition to an [StringSelectMenu] with an independent custom id.
    ///
    /// @return the [StringSelectMenu]
    /// @see CustomId#independent(String)
    @NotNull
    @Override
    public StringSelectMenu toJDAEntity() {
        return toJDAEntity(CustomId.independent(definitionId()));
    }

    /// Transforms this definition to an [StringSelectMenu] with the given [CustomId].
    ///
    /// @param customId the [CustomId] to use
    /// @return the [StringSelectMenu]
    @NotNull
    @Override
    public StringSelectMenu toJDAEntity(@NotNull CustomId customId) {
        return StringSelectMenu.create(customId.id())
                .setPlaceholder(placeholder)
                .setRequiredRange(minValue, maxValue)
                .addOptions(selectOptions.stream().map(SelectOptionDefinition::toJDAEntity).collect(Collectors.toSet()))
                .setDefaultOptions(selectOptions.stream()
                        .filter(SelectOptionDefinition::isDefault)
                        .map(SelectOptionDefinition::toJDAEntity)
                        .collect(Collectors.toSet())
                )
                .build();
    }

    @NotNull
    @Override
    public String displayName() {
        return "Select Menu: %s".formatted(placeholder);
    }

    /// Representation of a select option for a string select menu defined by a
    /// [`SelectOption`][com.github.kaktushose.jda.commands.annotations.interactions.SelectOption].
    ///
    /// @param value       the value of the select option
    /// @param label       the label of the select option
    /// @param description the description of the select option
    /// @param emoji       the [Emoji] of the select option or `null`
    /// @param isDefault   whether the select option is a default value
    public record SelectOptionDefinition(@NotNull String value,
                                         @NotNull String label,
                                         @Nullable String description,
                                         @Nullable Emoji emoji,
                                         boolean isDefault
    ) implements JDAEntity<SelectOption>, Definition {

        public SelectOptionDefinition(@NotNull SelectOption option) {
            this(option.getValue(), option.getLabel(), option.getDescription(), option.getEmoji(), option.isDefault());
        }

        /// Constructs a new [SelectOptionDefinition] from the given
        /// [`SelectOption`][com.github.kaktushose.jda.commands.annotations.interactions.SelectOption].
        public static SelectOptionDefinition build(com.github.kaktushose.jda.commands.annotations.interactions.SelectOption option) {
            Emoji emoji;
            String emojiString = option.emoji();
            if (emojiString.isEmpty()) {
                emoji = null;
            } else {
                emoji = Emoji.fromFormatted(emojiString);
            }
            return new SelectOptionDefinition(option.value(), option.label(), option.description(), emoji, option.isDefault());
        }

        @NotNull
        @Override
        public String displayName() {
            return value;
        }

        /// Transforms this definition into a [SelectOption].
        @NotNull
        @Override
        public SelectOption toJDAEntity() {
            return SelectOption.of(label, value)
                    .withDescription(description)
                    .withEmoji(emoji);
        }
    }
}
