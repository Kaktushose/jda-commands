package io.github.kaktushose.jdac.definitions.interactions;

import io.github.kaktushose.jdac.definitions.description.ClassDescription;
import io.github.kaktushose.jdac.definitions.description.MethodDescription;
import io.github.kaktushose.jdac.definitions.features.CustomIdJDAEntity;
import io.github.kaktushose.jdac.definitions.interactions.component.ComponentDefinition;
import io.github.kaktushose.jdac.dispatching.events.interactions.ModalEvent;
import io.github.kaktushose.jdac.internal.Helpers;
import net.dv8tion.jda.api.components.ModalTopLevelComponent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.modals.Modal;
import org.jspecify.annotations.Nullable;

import java.util.*;

/// Representation of a modal.
///
/// @param classDescription  the [ClassDescription] of the declaring class of the [#methodDescription()]
/// @param methodDescription the [MethodDescription] of the method this definition is bound to
/// @param permissions       a [Collection] of permissions for this modal
/// @param title             the title of the modal
public record ModalDefinition(
        ClassDescription classDescription,
        MethodDescription methodDescription,
        Collection<String> permissions,
        String title,
        Collection<ModalTopLevelComponent> components
) implements InteractionDefinition, CustomIdJDAEntity<Modal> {

    /// Builds a new [ModalDefinition] from the given [MethodBuildContext].
    ///
    /// @return an [Optional] holding the [ModalDefinition]
    public static ModalDefinition build(MethodBuildContext context) {
        var method = context.method();
        var modal = method.annotation(io.github.kaktushose.jdac.annotations.interactions.Modal.class);

        Helpers.checkSignature(method, List.of(ModalEvent.class, List.class));

        return new ModalDefinition(context.clazz(), method, Helpers.permissions(context), modal.value(), List.of());
    }

    /// Builds a new [ModalDefinition] with the given values.
    public ModalDefinition with(@Nullable String title, @Nullable Collection<ModalTopLevelComponent> components) {
        return new ModalDefinition(
                classDescription,
                methodDescription,
                permissions,
                ComponentDefinition.override(this.title, title),
                ComponentDefinition.override(this.components, components)
        );
    }

    /// Transforms this definition to an [Modal] with the given custom id.
    ///
    /// @return the [Modal]
    /// @see CustomId#independent(String)
    public Modal toJDAEntity(CustomId customId) {
        try {
            return Modal.create(customId.merged(), title).addComponents(components).build();
        } catch (IllegalArgumentException e) {
            throw Helpers.jdaException(e, this);
        }
    }

    @Override
    public String displayName() {
        return "Modal: %s".formatted(title);
    }
}
