package com.github.kaktushose.jda.commands.definitions.interactions.component.menu;

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

public record StringSelectMenuDefinition(
        @NotNull ClassDescription clazzDescription,
        @NotNull MethodDescription methodDescription,
        @NotNull Collection<String> permissions,
        @NotNull Set<SelectOptionDefinition> selectOptions,
        @NotNull String placeholder,
        int minValue,
        int maxValue
) implements SelectMenuDefinition<StringSelectMenu> {

    public static Optional<Definition> build(MethodBuildContext context) {
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
                .forEach(it -> {
                    selectOptions.add(SelectOptionDefinition.build(it));
                });

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

    @NotNull
    @Override
    public StringSelectMenu toJDAEntity() {
        return toJDAEntity(CustomId.independent(definitionId()));
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

    public record SelectOptionDefinition(@NotNull String value,
                                         @NotNull String label,
                                         @Nullable String description,
                                         @Nullable Emoji emoji,
                                         boolean isDefault
    ) implements JDAEntity<SelectOption>, Definition {

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

        @Override
        public @NotNull SelectOption toJDAEntity() {
            return SelectOption.of(label, value)
                    .withDescription(description)
                    .withEmoji(emoji);
        }
    }
}
