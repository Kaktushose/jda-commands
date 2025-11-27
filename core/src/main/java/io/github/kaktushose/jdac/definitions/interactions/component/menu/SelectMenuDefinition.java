package io.github.kaktushose.jdac.definitions.interactions.component.menu;

import io.github.kaktushose.jdac.definitions.interactions.component.ComponentDefinition;
import net.dv8tion.jda.api.components.selections.SelectMenu;

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
