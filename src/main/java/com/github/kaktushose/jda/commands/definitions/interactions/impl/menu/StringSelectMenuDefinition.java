package com.github.kaktushose.jda.commands.definitions.interactions.impl.menu;

import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.features.CustomIdJDAEntity;
import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.interactions.CustomId;
import com.github.kaktushose.jda.commands.definitions.interactions.Interaction;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.SequencedCollection;
import java.util.Set;
import java.util.stream.Collectors;

public record StringSelectMenuDefinition(
        @NotNull ClassDescription clazz,
        @NotNull MethodDescription method,
        @NotNull Collection<String> permissions,
        @NotNull Set<SelectOptionDefinition> selectOptions,
        @NotNull String placeholder,
        int minValue,
        int maxValue
) implements JDAEntity<StringSelectMenu>, CustomIdJDAEntity<StringSelectMenu>, Interaction {

    @NotNull
    @Override
    public StringSelectMenu toJDAEntity() {
        return toJDAEntity(new CustomId(definitionId()));
    }

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

    @Override
    public @NotNull SequencedCollection<Class<?>> methodSignature() {
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
