package com.github.kaktushose.jda.commands.definitions.features;

import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.interactions.ModalDefinition.TextInputDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.CommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.ContextCommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.OptionDataDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ButtonDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ComponentDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.EntitySelectMenuDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.StringSelectMenuDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.StringSelectMenuDefinition.SelectOptionDefinition;
import org.jetbrains.annotations.NotNull;

/// Indicates that the implementing [Definition] can be transformed into a JDA entity.
///
/// @see ButtonDefinition
/// @see EntitySelectMenuDefinition
/// @see StringSelectMenuDefinition
/// @see SlashCommandDefinition
/// @see ContextCommandDefinition
/// @see OptionDataDefinition
/// @see SelectOptionDefinition
/// @see TextInputDefinition
public sealed interface JDAEntity<T> extends Definition
        permits ComponentDefinition, TextInputDefinition, CommandDefinition, OptionDataDefinition, SelectOptionDefinition {

    /// Transforms this [Definition] into a JDA entity of the given type [T].
    ///
    /// @return a JDA entity of type [T]
    @NotNull
    T toJDAEntity();

}
