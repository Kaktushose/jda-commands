package io.github.kaktushose.jdac.dispatching.reply;

import io.github.kaktushose.jdac.annotations.interactions.Modal;
import io.github.kaktushose.jdac.definitions.interactions.CustomId;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.ModalDefinition;
import io.github.kaktushose.jdac.exceptions.InternalException;
import io.github.kaktushose.jdac.internal.logging.JDACLogger;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import io.github.kaktushose.jdac.message.resolver.ComponentResolver;
import io.github.kaktushose.jdac.message.resolver.MessageResolver;
import net.dv8tion.jda.api.components.ModalTopLevelComponent;
import net.dv8tion.jda.api.components.ModalTopLevelComponentUnion;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.label.LabelChildComponent;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.tree.ModalComponentTree;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Collection;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;
import static io.github.kaktushose.jdac.property.internal.IntrospectionAccess.*;

/// Handles all the business logic for sending modal replies.
@ApiStatus.Internal
public final class ModalReply extends ActionComponentResolver {

    private static final Logger log = JDACLogger.getLogger(ModalReply.class);

    /// Acknowledgement of this event with a [Modal]. This will open a popup on the target users Discord client.
    ///
    /// @param origin       the [Class] the modal handler is defined in
    /// @param modal        the method name of the [Modal] you want to reply with
    /// @param components   a [Collection] of [ModalTopLevelComponent]s to add to this modal
    /// @param placeholders the [Entry] placeholders to use for [message resolution][MessageResolver]
    /// @throws IllegalArgumentException if no [Modal] with the given name was found
    public void reply(
            @Nullable Class<?> origin,
            String modal,
            Collection<ModalTopLevelComponent> components,
            Entry... placeholders
    ) {
        if (!(scopedJdaEvent() instanceof IModalCallback callback)) {
            throw new InternalException("reply-failed", entry("event", scopedJdaEvent().getClass().getName()));
        }
        Checks.notEmpty(components, "Modal components");

        InteractionDefinition definition = scopedInvocationContext().definition();
        String className = origin == null ? definition.classDescription().name() : origin.getName();
        String definitionId = InteractionDefinition.createDefinitionId(className, modal);
        ModalDefinition modalDefinition = scopedInteractionRegistry().find(
                ModalDefinition.class,
                false,
                it -> it.definitionId().equals(definitionId)
        );

        // manual workaround for now
        ModalComponentTree componentTree = ModalComponentTree.of(components);
        componentTree = componentTree.replace(ComponentReplacer.of(
                Label.class,
                label -> label.getChild() instanceof Component<?, ?, ?, ?>,
                label -> label.withChild((LabelChildComponent) resolveActionComponent((Component<?, ?, ?, ?>) label.getChild()))
        ));

        var entryMap = Entry.toMap(placeholders);
        var resolver = new ComponentResolver<>(scopedMessageResolver(), ModalTopLevelComponentUnion.class);
        modalDefinition = modalDefinition.with(
                scopedMessageResolver().resolve(modalDefinition.title(), scopedUserLocale(), entryMap),
                resolver.resolve(componentTree.getComponents(), scopedUserLocale(), entryMap)
        );

        log.debug("Replying to interaction \"{}\" with Modal: \"{}\". [Runtime={}]", definition.displayName(), modalDefinition.displayName(), scopedRuntime().id());
        callback.replyModal(modalDefinition.toJDAEntity(new CustomId(scopedRuntime().id(), definitionId))).complete();
    }
}
