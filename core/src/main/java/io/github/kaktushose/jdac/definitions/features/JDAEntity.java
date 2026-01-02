package io.github.kaktushose.jdac.definitions.features;

import io.github.kaktushose.jdac.definitions.Definition;
import io.github.kaktushose.jdac.definitions.interactions.command.CommandDefinition;
import io.github.kaktushose.jdac.definitions.interactions.command.ContextCommandDefinition;
import io.github.kaktushose.jdac.definitions.interactions.command.OptionDataDefinition;
import io.github.kaktushose.jdac.definitions.interactions.command.SlashCommandDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.ButtonDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.ComponentDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.menu.EntitySelectMenuDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.menu.StringSelectMenuDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.menu.StringSelectMenuDefinition.MenuOptionDefinition;

/// Indicates that the implementing [Definition] can be transformed into a JDA entity.
///
/// @see ButtonDefinition
/// @see EntitySelectMenuDefinition
/// @see StringSelectMenuDefinition
/// @see SlashCommandDefinition
/// @see ContextCommandDefinition
/// @see OptionDataDefinition
/// @see MenuOptionDefinition
public sealed interface JDAEntity<T> extends Definition
        permits ComponentDefinition, CommandDefinition, OptionDataDefinition, MenuOptionDefinition {

    /// Transforms this [Definition] into a JDA entity of the given type [T].
    ///
    /// @return a JDA entity of type [T]
    T toJDAEntity();

}
