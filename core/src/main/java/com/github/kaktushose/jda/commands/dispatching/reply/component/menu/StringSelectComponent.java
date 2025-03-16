package com.github.kaktushose.jda.commands.dispatching.reply.component.menu;

import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.StringSelectMenuDefinition;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class StringSelectComponent extends SelectMenuComponent<StringSelectComponent, StringSelectMenu, StringSelectMenuDefinition> {

    private final Set<SelectOption> selectOptions = new HashSet<>();
    private final Collection<String> defaultValues = new HashSet<>();

    public StringSelectComponent(String method, Class<?> origin) {
        super(method, origin);
    }

    public StringSelectComponent selectOptions(Collection<SelectOption> selectOptions) {
        this.selectOptions.addAll(selectOptions);
        return this;
    }

    public StringSelectComponent selectOptions(SelectOption... selectOptions) {
        selectOptions(Arrays.asList(selectOptions));
        return this;
    }

    @NotNull
    public StringSelectComponent option(@NotNull String label, @NotNull String value, @Nullable String description, @Nullable Emoji emoji) {
        selectOptions.add(SelectOption.of(label, value)
                .withDescription(description)
                .withEmoji(emoji)
        );
        return this;
    }

    @NotNull
    public StringSelectComponent option(@NotNull String label, @NotNull String value) {
        return option(label, value, null, null);
    }

    @NotNull
    public StringSelectComponent option(@NotNull String label, @NotNull String value, @NotNull Emoji emoji) {
        return option(label, value, null, emoji);
    }

    @NotNull
    public StringSelectComponent option(@NotNull String label, @NotNull String value, @NotNull String description) {
        return option(label, value, description, null);
    }

    @NotNull
    public StringSelectComponent defaultValues(@NotNull Collection<String> values) {
        this.defaultValues.addAll(values);
        return this;
    }

    @NotNull
    public StringSelectComponent defaultValues(@NotNull String... values) {
        return defaultValues(Arrays.asList(values));
    }

    @NotNull
    public StringSelectComponent defaultOptions(@NotNull Collection<? extends SelectOption> values) {
        return defaultValues(values.stream().map(SelectOption::getValue).collect(Collectors.toSet()));
    }

    @NotNull
    public StringSelectComponent defaultOptions(@NotNull SelectOption... values) {
        return defaultOptions(Arrays.asList(values));
    }

    @Override
    public Class<StringSelectMenuDefinition> definitionClass() {
        return StringSelectMenuDefinition.class;
    }

    @Override
    public StringSelectMenuDefinition build(StringSelectMenuDefinition definition) {
        return definition.with(selectOptions, defaultValues, placeholder, minValues, maxValues);
    }
}
