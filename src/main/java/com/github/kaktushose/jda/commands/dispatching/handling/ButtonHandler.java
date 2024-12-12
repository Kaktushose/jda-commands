package com.github.kaktushose.jda.commands.dispatching.handling;

import com.github.kaktushose.jda.commands.dispatching.Invocation;
import com.github.kaktushose.jda.commands.dispatching.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.reflect.interactions.CustomId;
import com.github.kaktushose.jda.commands.reflect.interactions.components.ButtonDefinition;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.List;

public class ButtonHandler extends EventHandler<ButtonInteractionEvent> {

    public ButtonHandler(HandlerContext handlerContext) {
        super(handlerContext);
    }

    @Override
    protected Invocation<ButtonInteractionEvent> prepare(ButtonInteractionEvent event, Runtime runtime) {
        // ignore non jda-commands events
        if (CustomId.isInvalid(event.getComponentId())) {
            return null;
        }

        var button = interactionRegistry.find(ButtonDefinition.class, it ->
                it.getDefinitionId().equals(CustomId.getDefinitionId(event.getComponentId()))
        );
        var context = new InvocationContext<>(event, runtime.keyValueStore(), button, handlerContext, runtime.id());

        return new Invocation<>(
                context,
                runtime.instanceSupplier(),
                List.of(new ComponentEvent(event, interactionRegistry, runtime, context.ephemeral()))
        );
    }
}
