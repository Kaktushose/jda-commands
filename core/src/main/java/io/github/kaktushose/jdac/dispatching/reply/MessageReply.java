package io.github.kaktushose.jdac.dispatching.reply;

import io.github.kaktushose.jdac.definitions.interactions.CustomId;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition.ReplyConfig;
import io.github.kaktushose.jdac.definitions.interactions.InteractionRegistry;
import io.github.kaktushose.jdac.definitions.interactions.ModalDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.ButtonDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.ComponentDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.menu.SelectMenuDefinition;
import io.github.kaktushose.jdac.dispatching.reply.dynamic.ButtonComponent;
import io.github.kaktushose.jdac.dispatching.reply.dynamic.internal.UnspecificComponent;
import io.github.kaktushose.jdac.dispatching.reply.dynamic.menu.EntitySelectMenuComponent;
import io.github.kaktushose.jdac.dispatching.reply.dynamic.menu.StringSelectComponent;
import io.github.kaktushose.jdac.dispatching.reply.internal.MessageReplyAction;
import io.github.kaktushose.jdac.embeds.Embed;
import io.github.kaktushose.jdac.exceptions.InternalException;
import io.github.kaktushose.jdac.exceptions.internal.JDACException;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.components.ActionComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static io.github.kaktushose.jdac.dispatching.context.internal.RichInvocationContext.*;
import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

public final class MessageReply {

    private static final Logger log = LoggerFactory.getLogger(MessageReply.class);
    private final MessageReplyAction replyAction;

    public MessageReply(ReplyConfig replyConfig) {
        replyAction = new MessageReplyAction(replyConfig);
    }

    public MessageReply builder(Consumer<MessageCreateBuilder> builder) {
        replyAction.builder(builder);
        return this;
    }

    public MessageReply embeds(Embed... embeds) {
        replyAction.addEmbeds(Arrays.stream(embeds).map(Embed::build).toArray(MessageEmbed[]::new));
        return this;
    }

    public MessageReply embeds(String embed, Consumer<Embed> consumer) {
        Embed resolved = getFramework().embeds().get(embed, getJdaEvent().getUserLocale().toLocale());
        consumer.accept(resolved);
        replyAction.addEmbeds(resolved.build());
        return this;
    }

    public MessageReply embeds(String embed, Entry entry, Entry... entries) {
        Embed resolved = getFramework().embeds().get(embed, getJdaEvent().getUserLocale().toLocale());
        resolved.placeholders(entry).placeholders(entries);
        replyAction.addEmbeds(resolved.build());
        return this;
    }

    public MessageReply components(String... components) {
        return components(Arrays.stream(components).map(Component::enabled).toArray(Component[]::new));
    }

    public MessageReply components(Component<?, ?, ?, ?>... components) {
        List<ActionRowChildComponent> items = new ArrayList<>();
        for (Component<?, ?, ?, ?> component : components) {
            var className = component.origin().map(Class::getName)
                    .orElseGet(() -> getInvocationContext().definition().methodDescription().declaringClass().getName());
            String definitionId = InteractionDefinition.createDefinitionId(className, component.name());

            if (replyAction.components()
                    .stream()
                    .map(ActionComponent::getCustomId)
                    .filter(Objects::nonNull)
                    .map(CustomId::fromMerged)
                    .anyMatch(customId -> customId.definitionId().equals(definitionId))) {
                throw new IllegalArgumentException(
                        JDACException.errorMessage("duplicate-component", entry("method", "%s#%s".formatted(className, component.name())))
                );
            }

            var definition = findDefinition(component, definitionId, className);

            ActionRowChildComponent item = switch (definition) {
                case ButtonDefinition buttonDefinition -> {
                    var button = buttonDefinition.toJDAEntity().withDisabled(!component.enabled());
                    //only assign ids to non-link buttons
                    yield button.getUrl() == null ? button.withCustomId(createId(definition, component.independent()).merged()) : button;
                }

                case SelectMenuDefinition<?> menuDefinition -> {
                    var menu = menuDefinition.toJDAEntity(createId(definition, component.independent()));
                    yield menu.withDisabled(!component.enabled());
                }
            };

            item = switch (component) {
                case ButtonComponent buttonComponent -> buttonComponent.callback().apply((Button) item);
                case EntitySelectMenuComponent entitySelectMenuComponent ->
                        entitySelectMenuComponent.callback().apply(((EntitySelectMenu) item).createCopy()).build();
                case StringSelectComponent stringSelectComponent ->
                        stringSelectComponent.callback().apply(((StringSelectMenu) item).createCopy()).build();
                case UnspecificComponent unspecificComponent -> unspecificComponent.callback().apply(item);
            };

            item = resolve(item, component);

            items.add(item);

            log.debug("Reply Debug: Adding component \"{}\" to the reply", definition.displayName());
        }

        if (!items.isEmpty()) {
            replyAction.addComponents(ActionRow.of(items));
        }
        return this;
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

    @Nullable
    private <T> T orNull(@Nullable T val, Function<T, T> func) {
        if (val == null) return null;
        return func.apply(val);
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
