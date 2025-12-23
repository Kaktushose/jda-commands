package io.github.kaktushose.jdac.dispatching.handling;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.configuration.internal.Resolver;
import io.github.kaktushose.jdac.definitions.interactions.CustomId;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.ModalDefinition;
import io.github.kaktushose.jdac.dispatching.Runtime;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.dispatching.events.interactions.ModalEvent;
import io.github.kaktushose.jdac.internal.Helpers;
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
    protected InvocationContext<ModalInteractionEvent> prepare(ModalInteractionEvent event, Runtime runtime) {
        var modal = interactionRegistry.find(ModalDefinition.class, true, it ->
                it.definitionId().equals(CustomId.fromMerged(event.getModalId()).definitionId())
        );

        InteractionDefinition.ReplyConfig replyConfig = Helpers.replyConfig(modal, resolver.get(Property.GLOBAL_REPLY_CONFIG));

        List<Object> arguments = event.getValues().stream().map(ModalMapping::getAsString).collect(Collectors.toList());
        arguments.addFirst(new ModalEvent());

        return new InvocationContext<>(
                event,
                runtime.keyValueStore(),
                modal,
                replyConfig,
                Collections.unmodifiableList(arguments)
        );
    }
}
