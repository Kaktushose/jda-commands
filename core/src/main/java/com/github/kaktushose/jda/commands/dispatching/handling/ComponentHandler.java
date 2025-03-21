package com.github.kaktushose.jda.commands.dispatching.handling;

import com.github.kaktushose.jda.commands.definitions.interactions.CustomId;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ComponentDefinition;
import com.github.kaktushose.jda.commands.dispatching.DispatchingContext;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public final class ComponentHandler extends EventHandler<GenericComponentInteractionCreateEvent> {

    public ComponentHandler(DispatchingContext dispatchingContext) {
        super(dispatchingContext);
    }

    @Override
    protected InvocationContext<GenericComponentInteractionCreateEvent> prepare(@NotNull GenericComponentInteractionCreateEvent genericEvent, @NotNull Runtime runtime) {
        // ignore non jda-commands events
        if (CustomId.isInvalid(genericEvent.getComponentId())) {
            return null;
        }

        var component = registry.find(ComponentDefinition.class, true, it ->
                it.definitionId().equals(CustomId.fromEvent(genericEvent).definitionId())
        );

        List<Object> arguments = switch (genericEvent) {
            case StringSelectInteractionEvent event -> new ArrayList<>(List.of(event.getValues()));
            case EntitySelectInteractionEvent event -> new ArrayList<>(List.of(event.getMentions()));
            case ButtonInteractionEvent _ -> new ArrayList<>();
            default ->
                    throw new IllegalStateException("Should not occur. Please report this error the the devs of jda-commands.");
        };
        arguments.addFirst(new ComponentEvent(genericEvent, registry, runtime, component, dispatchingContext.globalReplyConfig()));

        return new InvocationContext<>(
                genericEvent,
                runtime.keyValueStore(),
                component,
                arguments
        );
    }
}
