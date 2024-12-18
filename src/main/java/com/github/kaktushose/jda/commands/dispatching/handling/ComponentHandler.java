package com.github.kaktushose.jda.commands.dispatching.handling;

import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.reflect.interactions.CustomId;
import com.github.kaktushose.jda.commands.reflect.interactions.components.GenericComponentDefinition;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

import java.util.List;

public final class ComponentHandler extends EventHandler<GenericComponentInteractionCreateEvent> {

    public ComponentHandler(HandlerContext handlerContext) {
        super(handlerContext);
    }

    @Override
    protected InvocationContext<GenericComponentInteractionCreateEvent> prepare(GenericComponentInteractionCreateEvent genericEvent, Runtime runtime) {
        // ignore non jda-commands events
        if (CustomId.isInvalid(genericEvent.getComponentId())) {
            return null;
        }

        var component = interactionRegistry.find(GenericComponentDefinition.class, true, it ->
                it.getDefinitionId().equals(CustomId.getDefinitionId(genericEvent.getComponentId()))
        );

        var componentEvent = new ComponentEvent(genericEvent, interactionRegistry, runtime, component.replyConfig());

        var arguments = switch (genericEvent) {
            case StringSelectInteractionEvent event -> List.of(componentEvent, event.getValues());
            case EntitySelectInteractionEvent event -> List.of(componentEvent, event.getMentions());
            default -> throw new IllegalStateException("Unexpected value: " + genericEvent);
        };

        return new InvocationContext<>(
                genericEvent,
                runtime.keyValueStore(),
                component,
                arguments
        );
    }
}
