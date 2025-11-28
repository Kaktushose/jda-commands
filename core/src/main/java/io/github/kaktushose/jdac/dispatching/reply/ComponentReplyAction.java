// TODO keep integrity, find a way to move this to "internal" again
package io.github.kaktushose.jdac.dispatching.reply;

import io.github.kaktushose.jdac.definitions.interactions.CustomId;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition.ReplyConfig;
import io.github.kaktushose.jdac.definitions.interactions.InteractionRegistry;
import io.github.kaktushose.jdac.definitions.interactions.ModalDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.ButtonDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.ComponentDefinition;
import io.github.kaktushose.jdac.dispatching.reply.dynamic.ButtonComponent;
import io.github.kaktushose.jdac.dispatching.reply.internal.ReplyAction;
import io.github.kaktushose.jdac.exceptions.InternalException;
import io.github.kaktushose.jdac.exceptions.internal.JDACException;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.tree.MessageComponentTree;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static io.github.kaktushose.jdac.dispatching.context.internal.RichInvocationContext.*;
import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

@ApiStatus.Internal
public final class ComponentReplyAction extends ReplyAction {

    private static final Logger log = LoggerFactory.getLogger(ComponentReplyAction.class);

    public ComponentReplyAction(ReplyConfig replyConfig, MessageTopLevelComponent component, MessageTopLevelComponent... components) {
        log.debug("Reply Debug: [Runtime={}]", getRuntime().id());
        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.useComponentsV2().addComponents(component).addComponents(components);
        super(replyConfig, builder);
    }

    @Override
    public Message reply() {
        MessageComponentTree componentTree = builder.getComponentTree();

        componentTree = componentTree.replace(ComponentReplacer.all(
                ComponentReplacer.of(ButtonComponent.class, _ -> true, component -> {
                    var className = component.origin().map(Class::getName)
                            .orElseGet(() -> getInvocationContext().definition().methodDescription().declaringClass().getName());
                    String definitionId = InteractionDefinition.createDefinitionId(className, component.name());

                    // TODO duplicate check

                    ButtonDefinition definition = findDefinition(component, definitionId, className);
                    Button button = definition.toJDAEntity().withDisabled(!component.enabled());
                    button = button.getUrl() == null
                            ? button.withCustomId(createId(definition, component.independent()).merged())
                            : button;
                    button = component.callback().apply(button);
                    return resolve(button, component);
                })
                // TODO add replacer for SelectMenus
        ));
        builder.setComponents(componentTree.getComponents());

        return super.reply();
    }

    @Override
    protected List<MessageTopLevelComponentUnion> retrieveComponents(Message original) {
        return List.of();
    }

    // TODO these are duplicated from MessageReply, find a solution
    @Nullable
    private <T> T orNull(@Nullable T val, Function<T, T> func) {
        if (val == null) return null;
        return func.apply(val);
    }

    private ActionRowChildComponent resolve(ActionRowChildComponent item, Component<?, ?, ?, ?> component) {
        return switch (item) {
            case Button button -> button.withLabel(resolve(button.getLabel(), component));
            case EntitySelectMenu menu ->
                    menu.createCopy().setPlaceholder(orNull(menu.getPlaceholder(), p -> resolve(p, component))).build();
            case StringSelectMenu menu -> {
                StringSelectMenu.Builder copy = menu.createCopy();
                List<SelectOption> localized = copy.getOptions()
                        .stream()
                        .map(option -> option.withDescription(orNull(option.getDescription(), d -> resolve(d, component)))
                                .withLabel(resolve(option.getLabel(), component)))
                        .toList();
                copy.getOptions().clear();
                copy.addOptions(localized);
                copy.setPlaceholder(orNull(copy.getPlaceholder(), p -> resolve(p, component)));
                yield copy.build();
            }
            default -> throw new InternalException("default-switch");
        };
    }

    private String resolve(String key, Component<?, ?, ?, ?> component) {
        return getFramework().messageResolver().resolve(key, getJdaEvent().getUserLocale().toLocale(), component.placeholder());
    }

    private <D extends ComponentDefinition<?>, T extends Component<T, ?, ?, D>> D findDefinition(Component<T, ?, ?, D> component, String definitionId, String className) {
        InteractionRegistry registry = getFramework().interactionRegistry();

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

    private CustomId createId(InteractionDefinition definition, boolean staticComponent) {
        return staticComponent
                ? CustomId.independent(definition.definitionId())
                : new CustomId(getRuntime().id(), definition.definitionId());
    }
}
