package com.github.kaktushose.jda.commands.dispatching.reply.dynamic;

import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import org.jetbrains.annotations.NotNull;

public abstract sealed class SelectMenuBuilder<T extends SelectMenu, B extends SelectMenu.Builder<T, B>>
        permits EntitySelectMenuBuilder, StringSelectMenuBuilder {

    protected final B menu;

    public SelectMenuBuilder(B menu) {
        this.menu = menu;
    }

    @NotNull
    public SelectMenuBuilder<T, B> placeholder(@NotNull String placeholder) {
        menu.setPlaceholder(placeholder);
        return this;
    }

    @NotNull
    public SelectMenuBuilder<T, B> min(int minValues) {
        menu.setMinValues(minValues);
        return this;
    }

    @NotNull
    public SelectMenuBuilder<T, B> max(int maxValues) {
        menu.setMaxValues(maxValues);
        return this;
    }

    @NotNull
    public SelectMenuBuilder<T, B> range(int min, int max) {
        menu.setRequiredRange(min, max);
        return this;
    }

    @NotNull
    public SelectMenuBuilder<T, B> enabled() {
        menu.setDisabled(false);
        return this;
    }

    @NotNull
    public SelectMenuBuilder<T, B> disabled() {
        menu.setDisabled(true);
        return this;
    }

}
