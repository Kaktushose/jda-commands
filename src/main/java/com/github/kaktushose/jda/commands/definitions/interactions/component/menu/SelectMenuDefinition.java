package com.github.kaktushose.jda.commands.definitions.interactions.component.menu;

import com.github.kaktushose.jda.commands.definitions.interactions.component.ComponentDefinition;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import org.jetbrains.annotations.NotNull;

public sealed interface SelectMenuDefinition<T extends SelectMenu> extends ComponentDefinition<T>
        permits EntitySelectMenuDefinition, StringSelectMenuDefinition {

    @NotNull String placeholder();

    int minValue();

    int maxValue();

}
