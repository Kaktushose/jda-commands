package io.github.kaktushose.jdac.dispatching.handling;

import io.github.kaktushose.jdac.definitions.interactions.CustomId;
import io.github.kaktushose.jdac.definitions.interactions.ModalDefinition;
import io.github.kaktushose.jdac.dispatching.Runtime;
import io.github.kaktushose.jdac.dispatching.events.interactions.ModalEvent;
import io.github.kaktushose.jdac.introspection.internal.IntrospectionImpl;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public final class ModalHandler extends EventHandler<ModalInteractionEvent> {

    public ModalHandler(IntrospectionImpl introspection) {
        super(introspection);
    }

    @Override
    protected PreparationResult prepare(ModalInteractionEvent event, Runtime runtime) {
        var modal = interactionRegistry.find(ModalDefinition.class, true, it ->
                it.definitionId().equals(CustomId.fromMerged(event.getModalId()).definitionId())
        );

        return new PreparationResult(modal, List.of(new ModalEvent()));
    }
}
