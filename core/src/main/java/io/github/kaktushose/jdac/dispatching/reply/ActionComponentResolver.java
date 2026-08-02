package io.github.kaktushose.jdac.dispatching.reply;

import io.github.kaktushose.jdac.definitions.interactions.CustomId;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.InteractionRegistry;
import io.github.kaktushose.jdac.definitions.interactions.ModalDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.ButtonDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.ComponentDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.menu.SelectMenuDefinition;
import io.github.kaktushose.jdac.dispatching.reply.dynamic.ButtonComponent;
import io.github.kaktushose.jdac.dispatching.reply.dynamic.internal.UnspecificComponent;
import io.github.kaktushose.jdac.dispatching.reply.dynamic.menu.EntitySelectMenuComponent;
import io.github.kaktushose.jdac.dispatching.reply.dynamic.menu.StringSelectComponent;
import io.github.kaktushose.jdac.exceptions.internal.JDACException;
import io.github.kaktushose.jdac.internal.logging.JDACLogger;
import io.github.kaktushose.jdac.message.resolver.ComponentResolver;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Objects;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;
import static io.github.kaktushose.jdac.property.internal.IntrospectionAccess.*;

sealed class ActionComponentResolver permits MessageReply, ModalReply {

    private static final Logger log = JDACLogger.getLogger(ActionComponentResolver.class);
    private final ComponentResolver<ActionRowChildComponent> resolver;

    protected ActionComponentResolver() {
        this.resolver = new ComponentResolver<>(scopedMessageResolver(), ActionRowChildComponent.class);
    }

    protected ComponentReplacer resolver() {
        return ComponentReplacer.of(Component.class, _ -> true, this::resolveActionComponent);
    }

    protected ActionRowChildComponent resolveActionComponent(Component<?, ?, ?, ?> component) {
        var className = component.origin().map(Class::getName)
                .orElseGet(() -> scopedInvocationContext().definition().methodDescription().declaringClass().getName());
        String definitionId = InteractionDefinition.createDefinitionId(className, component.name());

        var definition = findDefinition(component, definitionId, className);

        int uniqueId = Objects.requireNonNullElse(definition.uniqueId(), -1);
        ActionRowChildComponent item = switch (definition) {
            case ButtonDefinition buttonDefinition ->
                    buttonDefinition.toJDAEntity(createId(definition, component.independent())).withDisabled(!component.enabled());
            case SelectMenuDefinition<?> menuDefinition ->
                    menuDefinition.toJDAEntity(createId(definition, component.independent())).withDisabled(!component.enabled());
        };

        item = switch (component) {
            case ButtonComponent buttonComponent -> buttonComponent.callback().apply((Button) item);
            case EntitySelectMenuComponent entitySelectMenuComponent ->
                    entitySelectMenuComponent.callback().apply(((EntitySelectMenu) item).createCopy()).build();
            case StringSelectComponent stringSelectComponent ->
                    stringSelectComponent.callback().apply(((StringSelectMenu) item).createCopy()).build();
            case UnspecificComponent unspecificComponent -> unspecificComponent.callback().apply(item);
        };

        if (uniqueId > 0) {
            item = item.withUniqueId(uniqueId);
        }
        item = resolver.resolve(item, scopedUserLocale(), component.placeholder());
        log.debug("Reply Debug: Adding component \"{}\" to the reply", definition.displayName());
        return item;
    }

    private <D extends ComponentDefinition<?>, T extends Component<T, ?, ?, D>> D findDefinition(Component<T, ?, ?, D> component, String definitionId, String className) {
        InteractionRegistry registry = scopedInteractionRegistry();

        try {
            // this cast is effective safe
            D definition = registry.find(component.definitionClass(), false, it ->
                    it.definitionId().equals(definitionId)
            );

            return component.build(definition);
        } catch (IllegalArgumentException e) { // only check if search failed
            Collection<ModalDefinition> found = registry.find(ModalDefinition.class, it -> it.definitionId().equals(definitionId));
            if (!found.isEmpty()) {
                throw new IllegalArgumentException(
                        JDACException.errorMessage("modal-as-component", entry("method", "%s#%s".formatted(className, component.name())))
                );
            }
            throw e;
        }
    }

    private CustomId createId(InteractionDefinition definition, boolean independent) {
        return independent
                ? CustomId.independent(definition.definitionId())
                : new CustomId(scopedRuntime().id(), definition.definitionId());
    }
}
