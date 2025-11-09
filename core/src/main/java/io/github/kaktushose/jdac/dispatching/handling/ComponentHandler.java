package io.github.kaktushose.jdac.dispatching.handling;

import io.github.kaktushose.jdac.definitions.interactions.CustomId;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.ComponentDefinition;
import io.github.kaktushose.jdac.dispatching.FrameworkContext;
import io.github.kaktushose.jdac.dispatching.Runtime;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent;
import io.github.kaktushose.jdac.exceptions.InternalException;
import io.github.kaktushose.jdac.internal.Helpers;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public final class ComponentHandler extends EventHandler<GenericComponentInteractionCreateEvent> {

    public ComponentHandler(FrameworkContext context) {
        super(context);
    }

    @Override
    protected InvocationContext<GenericComponentInteractionCreateEvent> prepare(GenericComponentInteractionCreateEvent genericEvent, Runtime runtime) {
        var component = interactionRegistry.find(ComponentDefinition.class, true, it ->
                it.definitionId().equals(CustomId.fromMerged(genericEvent.getComponentId()).definitionId())
        );

        InteractionDefinition.ReplyConfig replyConfig = Helpers.replyConfig(component, context.globalReplyConfig());

        List<Object> arguments = switch (genericEvent) {
            case StringSelectInteractionEvent event -> new ArrayList<>(List.of(event.getValues()));
            case EntitySelectInteractionEvent event -> new ArrayList<>(List.of(event.getMentions()));
            case ButtonInteractionEvent _ -> new ArrayList<>();
            default -> throw new InternalException("default-switch");
        };
        arguments.addFirst(new ComponentEvent());

        return new InvocationContext<>(
                new InvocationContext.Utility(context.i18n(), context.messageResolver()),
                new InvocationContext.Data<>(
                    genericEvent,
                    runtime.keyValueStore(),
                    component,
                    replyConfig,
                    arguments
                )
        );
    }
}
