package io.github.kaktushose.jdac.definitions.features;

import io.github.kaktushose.jdac.definitions.Definition;
import io.github.kaktushose.jdac.definitions.interactions.CustomId;
import io.github.kaktushose.jdac.definitions.interactions.ModalDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.ButtonDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.ComponentDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.menu.EntitySelectMenuDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.menu.StringSelectMenuDefinition;

/// Indicates that the implementing [Definition] can be transformed into a JDA entity that has a [CustomId].
///
/// @see ButtonDefinition
/// @see EntitySelectMenuDefinition
/// @see StringSelectMenuDefinition
/// @see ModalDefinition
public sealed interface CustomIdJDAEntity<T> extends JDAEntity<T> permits ComponentDefinition, ModalDefinition {

    /// Transforms this [Definition] into a JDA entity of the given type [T].
    ///
    /// @param customId the [CustomId] to use to build this JDA entity
    /// @return a JDA entity of type [T]

    T toJDAEntity(CustomId customId);

}
