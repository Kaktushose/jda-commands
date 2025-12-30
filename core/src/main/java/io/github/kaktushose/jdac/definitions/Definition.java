package io.github.kaktushose.jdac.definitions;

import io.github.kaktushose.jdac.definitions.features.JDAEntity;
import io.github.kaktushose.jdac.definitions.features.internal.Invokable;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.ModalDefinition;
import io.github.kaktushose.jdac.definitions.interactions.command.OptionDataDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.menu.StringSelectMenuDefinition;

/// The common interface for all interaction definitions and their sub parts, such as parameters or text inputs, etc.
public sealed interface Definition permits JDAEntity, Invokable, InteractionDefinition, ModalDefinition.TextInputDefinition, OptionDataDefinition, OptionDataDefinition.ConstraintDefinition, StringSelectMenuDefinition.MenuOptionDefinition {

    /// The id for this definition. Per default this is the hash code of the [Object#toString()] method.
    default String definitionId() {
        return String.valueOf(toString().hashCode());
    }

    /// The human-readable name of this definition.
    String displayName();
}
