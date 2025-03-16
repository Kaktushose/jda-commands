package com.github.kaktushose.jda.commands.dispatching.reply.component.menu;

import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.StringSelectMenuDefinition;
import com.github.kaktushose.jda.commands.dispatching.reply.Component;
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

/// An implementation of [Component] specific to [StringSelectMenu]
public final class StringSelectComponent extends SelectMenuComponent<StringSelectComponent, StringSelectMenu, StringSelectMenuDefinition> {

    private final Set<SelectOption> selectOptions = new HashSet<>();
    private final Collection<String> defaultValues = new HashSet<>();

    public StringSelectComponent(@NotNull String method, @NotNull Class<?> origin) {
        super(method, origin);
    }

    /// @see StringSelectMenu.Builder#addOptions(Collection) 
    public StringSelectComponent selectOptions(@NotNull Collection<SelectOption> selectOptions) {
        this.selectOptions.addAll(selectOptions);
        return this;
    }

    /// @see StringSelectMenu.Builder#addOptions(SelectOption...) 
    public StringSelectComponent selectOptions(@NotNull SelectOption... selectOptions) {
        selectOptions(Arrays.asList(selectOptions));
        return this;
    }

    /// @see StringSelectMenu.Builder#addOption(String, String, String, Emoji) 
    @NotNull
    public StringSelectComponent option(@NotNull String label, @NotNull String value, @Nullable String description, @Nullable Emoji emoji) {
        selectOptions.add(SelectOption.of(label, value)
                .withDescription(description)
                .withEmoji(emoji)
        );
        return this;
    }

    /// @see StringSelectMenu.Builder#addOption(String, String) 
    @NotNull
    public StringSelectComponent option(@NotNull String label, @NotNull String value) {
        return option(label, value, null, null);
    }

    /// @see StringSelectMenu.Builder#addOption(String, String, Emoji) 
    @NotNull
    public StringSelectComponent option(@NotNull String label, @NotNull String value, @NotNull Emoji emoji) {
        return option(label, value, null, emoji);
    }

    /// @see StringSelectMenu.Builder#addOption(String, String, String, Emoji)
    @NotNull
    public StringSelectComponent option(@NotNull String label, @NotNull String value, @NotNull String description) {
        return option(label, value, description, null);
    }

    /// @see StringSelectMenu.Builder#setDefaultValues(Collection)
    @NotNull
    public StringSelectComponent defaultValues(@NotNull Collection<String> values) {
        this.defaultValues.addAll(values);
        return this;
    }

    /// @see StringSelectMenu.Builder#setDefaultValues(String...)
    @NotNull
    public StringSelectComponent defaultValues(@NotNull String... values) {
        return defaultValues(Arrays.asList(values));
    }

    /// @see StringSelectMenu.Builder#setDefaultOptions(Collection)
    @NotNull
    public StringSelectComponent defaultOptions(@NotNull Collection<? extends SelectOption> values) {
        return defaultValues(values.stream().map(SelectOption::getValue).collect(Collectors.toSet()));
    }

    /// @see StringSelectMenu.Builder#setDefaultValues(String...)
    @NotNull
    public StringSelectComponent defaultOptions(@NotNull SelectOption... values) {
        return defaultOptions(Arrays.asList(values));
    }

    @Override
    protected Class<StringSelectMenuDefinition> definitionClass() {
        return StringSelectMenuDefinition.class;
    }

    @Override
    protected StringSelectMenuDefinition build(StringSelectMenuDefinition definition) {
        return definition.with(selectOptions, defaultValues, placeholder, minValues, maxValues);
    }
}
