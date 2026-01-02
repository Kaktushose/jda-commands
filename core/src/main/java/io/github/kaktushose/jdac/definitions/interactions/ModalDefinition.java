package io.github.kaktushose.jdac.definitions.interactions;

import io.github.kaktushose.jdac.annotations.interactions.Mapped;
import io.github.kaktushose.jdac.definitions.description.ClassDescription;
import io.github.kaktushose.jdac.definitions.description.MethodDescription;
import io.github.kaktushose.jdac.definitions.features.CustomIdJDAEntity;
import io.github.kaktushose.jdac.definitions.interactions.component.ComponentDefinition;
import io.github.kaktushose.jdac.dispatching.events.interactions.ModalEvent;
import io.github.kaktushose.jdac.exceptions.InvalidDeclarationException;
import io.github.kaktushose.jdac.internal.Helpers;
import net.dv8tion.jda.api.components.ModalTopLevelComponent;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.modals.Modal;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

/// Representation of a modal.
///
/// @param classDescription  the [ClassDescription] of the declaring class of the [#methodDescription()]
/// @param methodDescription the [MethodDescription] of the method this definition is bound to
/// @param parameters        the [MappedParameter]s
/// @param permissions       a [Collection] of permissions for this modal
/// @param title             the title of the modal
/// @param components        a [Collection] of [ModalTopLevelComponent]s that will be added to this modal
public record ModalDefinition(
        ClassDescription classDescription,
        MethodDescription methodDescription,
        Collection<MappedParameter> parameters,
        Collection<String> permissions,
        String title,
        Collection<ModalTopLevelComponent> components
) implements InteractionDefinition, CustomIdJDAEntity<Modal> {

    private static final Map<Class<?>, Mapping> CLASS_TO_MAPPING = Map.of(
            String.class, Mapping.STRING,
            Mentions.class, Mapping.MENTIONS,
            List.class, Mapping.ATTACHMENT_LIST
    );

    /// Builds a new [ModalDefinition] from the given [MethodBuildContext].
    ///
    /// @return an [Optional] holding the [ModalDefinition]
    public static ModalDefinition build(MethodBuildContext context) {
        var method = context.method();
        var modal = method.annotation(io.github.kaktushose.jdac.annotations.interactions.Modal.class);

        Helpers.checkSignatureModal(method);

        List<MappedParameter> parameters = method.parameters().stream()
                .filter(it -> !it.type().equals(ModalEvent.class))
                .map(it -> {
                    String id = it.annotation(Mapped.class).value();
                    if (id.isBlank()) {
                        id = it.name();
                    }
                    return new MappedParameter(it.type(), id);
                }).toList();

        return new ModalDefinition(context.clazz(), method, parameters, Helpers.permissions(context), modal.value(), List.of());
    }

    /// Builds a new [ModalDefinition] with the given values.
    public ModalDefinition with(@Nullable String title, @Nullable Collection<ModalTopLevelComponent> components) {
        return new ModalDefinition(
                classDescription,
                methodDescription,
                parameters,
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

    public record MappedParameter(Mapping mapping, String id) {

        public MappedParameter(Class<?> type, String id) {
            Mapping mapped = CLASS_TO_MAPPING.get(type);
            if (mapped == null) {
                throw new InvalidDeclarationException("unsupported-modal-mapping", entry("type", type.getName()));
            }
            this(mapped, id);
        }
    }

    public enum Mapping {
        STRING,
        MENTIONS,
        ATTACHMENT_LIST
    }
}
