package com.github.kaktushose.jda.commands.definitions.interactions.impl;

import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.features.Replyable;
import com.github.kaktushose.jda.commands.definitions.interactions.CustomIdInteraction;
import com.github.kaktushose.jda.commands.definitions.interactions.PermissionsInteraction;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.SequencedCollection;
import java.util.Set;
import java.util.stream.Collectors;

public record StringSelectMenuDefinition(
        @NotNull Method method,
        @NotNull Collection<String> permissions,
        @NotNull Set<SelectOptionDefinition> selectOptions,
        @NotNull String placeholder,
        int minValue,
        int maxValue
) implements JDAEntity<StringSelectMenu>, Replyable, PermissionsInteraction, CustomIdInteraction {

    @NotNull
    @Override
    public StringSelectMenu toJDAEntity() {
        return toSelectMenu(independentCustomId().customId());
    }

    @NotNull
    @Override
    public StringSelectMenu toJDAEntity(@NotNull String runtimeId) {
        return toSelectMenu(boundCustomId(runtimeId).customId());
    }

    @NotNull
    private StringSelectMenu toSelectMenu(@NotNull String customId) {
        return StringSelectMenu.create(boundCustomId(customId).customId())
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

    @Override
    public @NotNull SequencedCollection<Class<?>> parameters() {
        return List.of(ComponentEvent.class, List.class);
    }

    public record SelectOptionDefinition(@NotNull String value,
                                  @NotNull String label,
                                  @Nullable String description,
                                  @Nullable Emoji emoji,
                                  boolean isDefault
    ) implements JDAEntity<SelectOption>, Definition {

        @NotNull
        @Override
        public String displayName() {
            return value;
        }

        @Override
        public @NotNull SelectOption toJDAEntity() {
            return SelectOption.of(label, value)
                    .withDescription(description)
                    .withEmoji(emoji);
        }
    }
}
