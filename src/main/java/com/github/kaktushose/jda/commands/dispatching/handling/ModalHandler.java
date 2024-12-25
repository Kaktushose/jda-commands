package com.github.kaktushose.jda.commands.dispatching.handling;

import com.github.kaktushose.jda.commands.dispatching.internal.Runtime;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ModalEvent;
import com.github.kaktushose.jda.commands.reflect.interactions.CustomId;
import com.github.kaktushose.jda.commands.reflect.interactions.ModalDefinition;
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
        // ignore non jda-commands events
        if (CustomId.isInvalid(event.getModalId())) {
            return null;
        }

        var modal = interactionRegistry.find(ModalDefinition.class, true, it ->
                it.getDefinitionId().equals(CustomId.definitionId(event.getModalId()))
        );

        List<Object> arguments = event.getValues().stream().map(ModalMapping::getAsString).collect(Collectors.toList());
        arguments.addFirst(new ModalEvent(event, interactionRegistry, runtime, modal));

        return new InvocationContext<>(
                event,
                runtime.keyValueStore(),
                modal,
                Collections.unmodifiableList(arguments)
        );
    }
}
