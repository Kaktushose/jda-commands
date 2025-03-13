package com.github.kaktushose.jda.commands.dispatching.reply.dynamic;

import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.StringSelectMenuDefinition;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public final class StringSelectMenuBuilder extends SelectMenuBuilder<StringSelectMenu, StringSelectMenu.Builder> {

    private final StringSelectMenuDefinition definition;

    public StringSelectMenuBuilder(StringSelectMenuDefinition definition) {
        super(definition.toJDAEntity().createCopy());
        this.definition = definition;
    }

    @NotNull
    public StringSelectMenuBuilder clear() {
        menu.getOptions().clear();
        return this;
    }

    @NotNull
    public StringSelectMenuBuilder options(@NotNull SelectOption... options) {
        menu.addOptions(options);
        return this;
    }

    @NotNull
    public StringSelectMenuBuilder options(@NotNull Collection<? extends SelectOption> options) {
        menu.addOptions(options);
        return this;
    }

    @NotNull
    public StringSelectMenuBuilder option(@NotNull String label, @NotNull String value) {
        menu.addOption(label, value);
        return this;
    }

    @NotNull
    public StringSelectMenuBuilder option(@NotNull String label, @NotNull String value, @NotNull Emoji emoji) {
        menu.addOption(label, value, emoji);
        return this;
    }

    @NotNull
    public StringSelectMenuBuilder option(@NotNull String label, @NotNull String value, @NotNull String description) {
        menu.addOption(label, value, description);
        return this;
    }

    @NotNull
    public StringSelectMenuBuilder option(@NotNull String label, @NotNull String value, @Nullable String description, @Nullable Emoji emoji) {
        menu.addOption(label, value, description, emoji);
        return this;
    }

    @NotNull
    public StringSelectMenuBuilder defaultValues(@NotNull Collection<String> values) {
        menu.setDefaultValues(values);
        return this;
    }

    @NotNull
    public StringSelectMenuBuilder defaultValues(@NotNull String... values) {
        menu.setDefaultValues(values);
        return this;
    }

    @NotNull
    public StringSelectMenuBuilder defaultOptions(@NotNull Collection<? extends SelectOption> values) {
        menu.setDefaultOptions(values);
        return this;
    }

    @NotNull
    public StringSelectMenuBuilder defaultOptions(@NotNull SelectOption... values) {
        menu.setDefaultOptions(values);
        return this;
    }

    @Override
    public StringSelectMenuDefinition build() {
        return new StringSelectMenuDefinition(definition, menu.build());
    }
}
