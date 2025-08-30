package com.github.kaktushose.jda.commands.dispatching.reply.dynamic.menu;

import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.StringSelectMenuDefinition;
import com.github.kaktushose.jda.commands.dispatching.reply.Component;
import com.github.kaktushose.jda.commands.i18n.I18n;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.selections.StringSelectMenu.Builder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/// An implementation of [Component] specific to [StringSelectMenu]
public final class StringSelectComponent extends SelectMenuComponent<StringSelectComponent, StringSelectMenu, Builder, StringSelectMenuDefinition> {

    private final Set<SelectOption> selectOptions = new HashSet<>();
    private final Collection<String> defaultValues = new HashSet<>();

    public StringSelectComponent(String method, @Nullable Class<?> origin, I18n.Entry[] placeholder) {
        super(method, origin, placeholder);
    }

    /// @see StringSelectMenu.Builder#addOptions(Collection)
    public StringSelectComponent selectOptions(Collection<SelectOption> selectOptions) {
        this.selectOptions.addAll(selectOptions);
        return this;
    }

    /// @see StringSelectMenu.Builder#addOptions(SelectOption...)
    public StringSelectComponent selectOptions(SelectOption... selectOptions) {
        selectOptions(Arrays.asList(selectOptions));
        return this;
    }

    /// @see StringSelectMenu.Builder#addOption(String, String, String, Emoji)
    public StringSelectComponent option(String label, String value, @Nullable String description, @Nullable Emoji emoji) {
        selectOptions.add(SelectOption.of(label, value)
                .withDescription(description)
                .withEmoji(emoji)
        );
        return this;
    }

    /// @see StringSelectMenu.Builder#addOption(String, String)
    public StringSelectComponent option(String label, String value) {
        return option(label, value, null, null);
    }

    /// @see StringSelectMenu.Builder#addOption(String, String, Emoji)
    public StringSelectComponent option(String label, String value, Emoji emoji) {
        return option(label, value, null, emoji);
    }

    /// @see StringSelectMenu.Builder#addOption(String, String, String)
    public StringSelectComponent option(String label, String value, String description) {
        return option(label, value, description, null);
    }

    /// @see StringSelectMenu.Builder#setDefaultValues(Collection)
    public StringSelectComponent defaultValues(Collection<String> values) {
        this.defaultValues.addAll(values);
        return this;
    }

    /// @see StringSelectMenu.Builder#setDefaultValues(String...)
    public StringSelectComponent defaultValues(String... values) {
        return defaultValues(Arrays.asList(values));
    }

    /// @see StringSelectMenu.Builder#setDefaultOptions(Collection)
    public StringSelectComponent defaultOptions(Collection<? extends SelectOption> values) {
        return defaultValues(values.stream().map(SelectOption::getValue).collect(Collectors.toSet()));
    }

    /// @see StringSelectMenu.Builder#setDefaultValues(String...)
    public StringSelectComponent defaultOptions(SelectOption... values) {
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
