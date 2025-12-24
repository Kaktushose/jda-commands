package io.github.kaktushose.jdac.dispatching.handling;

import io.github.kaktushose.jdac.configuration.internal.Resolver;
import io.github.kaktushose.jdac.definitions.interactions.CustomId;
import io.github.kaktushose.jdac.definitions.interactions.ModalDefinition;
import io.github.kaktushose.jdac.dispatching.Runtime;
import io.github.kaktushose.jdac.dispatching.events.interactions.ModalEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ApiStatus.Internal
public final class ModalHandler extends EventHandler<ModalInteractionEvent> {

    public ModalHandler(Resolver resolver) {
        super(resolver);
    }

    @Override
    protected Ingredients prepare(ModalInteractionEvent event, Runtime runtime) {
        var modal = interactionRegistry.find(ModalDefinition.class, true, it ->
                it.definitionId().equals(CustomId.fromMerged(event.getModalId()).definitionId())
        );

        List<Object> arguments = event.getValues().stream().map(ModalMapping::getAsString).collect(Collectors.toList());
        arguments.addFirst(new ModalEvent());

        return new Ingredients(modal, Collections.unmodifiableList(arguments));
    }
}
