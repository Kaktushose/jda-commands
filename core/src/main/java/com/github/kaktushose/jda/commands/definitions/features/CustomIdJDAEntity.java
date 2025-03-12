package com.github.kaktushose.jda.commands.definitions.features;

import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.interactions.CustomId;
import com.github.kaktushose.jda.commands.definitions.interactions.ModalDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ButtonDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ComponentDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.EntitySelectMenuDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.StringSelectMenuDefinition;
import com.github.kaktushose.jda.commands.i18n.I18nData;
import org.jetbrains.annotations.NotNull;

/// Indicates that the implementing [Definition] can be transformed into a JDA entity that has a [CustomId].
///
/// @see ButtonDefinition
/// @see EntitySelectMenuDefinition
/// @see StringSelectMenuDefinition
/// @see ModalDefinition
public sealed interface CustomIdJDAEntity<T> extends Definition permits ComponentDefinition, ModalDefinition {

    /// Transforms this [Definition] into a JDA entity of the given type [T].
    ///
    /// @param customId the [CustomId] to use to build this JDA entity
    /// @return a JDA entity of type [T]
    @NotNull
    T toJDAEntity(@NotNull CustomId customId, I18nData locData);

}
