package com.github.kaktushose.jda.commands.dispatching.handling;

import com.github.kaktushose.jda.commands.definitions.interactions.CustomId;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ComponentDefinition;
import com.github.kaktushose.jda.commands.dispatching.FrameworkContext;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.exceptions.InternalException;
import com.github.kaktushose.jda.commands.internal.Helpers;
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
