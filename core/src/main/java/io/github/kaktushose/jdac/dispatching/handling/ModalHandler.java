package io.github.kaktushose.jdac.dispatching.handling;

import io.github.kaktushose.jdac.definitions.interactions.CustomId;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.ModalDefinition;
import io.github.kaktushose.jdac.dispatching.FrameworkContext;
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

    public ModalHandler(FrameworkContext frameworkContext) {
        super(frameworkContext);
    }

    @Override
    protected InvocationContext<ModalInteractionEvent> prepare(ModalInteractionEvent event, Runtime runtime) {
        var modal = interactionRegistry.find(ModalDefinition.class, true, it ->
                it.definitionId().equals(CustomId.fromMerged(event.getModalId()).definitionId())
        );

        InteractionDefinition.ReplyConfig replyConfig = Helpers.replyConfig(modal, context.globalReplyConfig());

        List<Object> arguments = event.getValues().stream().map(ModalMapping::getAsString).collect(Collectors.toList());
        arguments.addFirst(new ModalEvent());

        return new InvocationContext<>(
                new InvocationContext.Utility(context.i18n(), context.messageResolver()),
                new InvocationContext.Data<>(
                    event,
                    runtime.keyValueStore(),
                    modal,
                    replyConfig,
                    Collections.unmodifiableList(arguments)
                )
        );
    }
}
