package io.github.kaktushose.jdac.dispatching.handling;

import io.github.kaktushose.jdac.definitions.interactions.CustomId;
import io.github.kaktushose.jdac.definitions.interactions.ModalDefinition;
import io.github.kaktushose.jdac.definitions.interactions.ModalDefinition.MappedParameter;
import io.github.kaktushose.jdac.dispatching.Runtime;
import io.github.kaktushose.jdac.dispatching.events.interactions.ModalEvent;
import io.github.kaktushose.jdac.introspection.internal.IntrospectionImpl;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public final class ModalHandler extends EventHandler<ModalInteractionEvent> {

    public ModalHandler(IntrospectionImpl introspection) {
        super(introspection);
    }

    @Override
    protected PreparationResult prepare(ModalInteractionEvent event, Runtime runtime) {
        var modal = interactionRegistry.find(ModalDefinition.class, true, it ->
                it.definitionId().equals(CustomId.fromMerged(event.getModalId()).definitionId())
        );

        List<@Nullable Object> arguments = new ArrayList<>();
        arguments.add(new ModalEvent());
        for (MappedParameter parameter : modal.parameters()) {
            ModalMapping value = event.getValue(parameter.id());
            if (value == null) {
                try {
                    value = event.getValueByUniqueId(Integer.parseInt(parameter.id()));
                } catch (NumberFormatException _) {}
            }
            if (value == null) {
                arguments.add(null);
            } else {
                Object argument = switch (parameter.mapping()) {
                    case STRING -> value.getAsString();
                    case MENTIONS -> value.getAsMentions();
                    case ATTACHMENT_LIST -> value.getAsAttachmentList();
                };
                arguments.add(argument);
            }
        }

        return new PreparationResult(modal, arguments);
    }
}
