package com.github.kaktushose.jda.commands.dispatching.handling;

import com.github.kaktushose.jda.commands.definitions.interactions.CustomId;
import com.github.kaktushose.jda.commands.definitions.interactions.ModalDefinition;
import com.github.kaktushose.jda.commands.dispatching.DispatchingContext;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ModalEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ModalHandler extends EventHandler<ModalInteractionEvent> {

    public ModalHandler(DispatchingContext dispatchingContext) {
        super(dispatchingContext);
    }

    @Override
    protected InvocationContext<ModalInteractionEvent> prepare(@NotNull ModalInteractionEvent event, @NotNull Runtime runtime) {
        var modal = registry.find(ModalDefinition.class, true, it ->
                it.definitionId().equals(CustomId.fromMerged(event.getModalId()).definitionId())
        );

        InteractionDefinition.ReplyConfig replyConfig = Helpers.replyConfig(modal, dispatchingContext.globalReplyConfig());

        List<Object> arguments = event.getValues().stream().map(ModalMapping::getAsString).collect(Collectors.toList());
        arguments.addFirst(new ModalEvent(event, registry, runtime, modal, replyConfig, dispatchingContext.embeds()));

        return new InvocationContext<>(
                event,
                runtime.keyValueStore(),
                modal,
                replyConfig,
                Collections.unmodifiableList(arguments)
        );
    }
}
