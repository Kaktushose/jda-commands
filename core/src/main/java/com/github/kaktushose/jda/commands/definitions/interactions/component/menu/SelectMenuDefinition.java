package com.github.kaktushose.jda.commands.definitions.interactions.component.menu;

import com.github.kaktushose.jda.commands.definitions.interactions.component.ComponentDefinition;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

/// Common interface for select menu definitions.
///
/// @see EntitySelectMenuDefinition
/// @see StringSelectMenuDefinition
public sealed interface SelectMenuDefinition<T extends SelectMenu> extends ComponentDefinition<T>
        permits EntitySelectMenuDefinition, StringSelectMenuDefinition {

    /// the placeholder text of this menu
    String placeholder();

    /// the minimum amount of choices
    int minValue();

    /// the maximum amount of choices
    int maxValue();

}
