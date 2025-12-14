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
import io.github.kaktushose.jdac.dispatching.reply.internal.ReplyAction;
import io.github.kaktushose.jdac.embeds.Embed;
import io.github.kaktushose.jdac.embeds.EmbedConfig;
import io.github.kaktushose.jdac.exceptions.InternalException;
import io.github.kaktushose.jdac.exceptions.internal.JDACException;
import io.github.kaktushose.jdac.message.i18n.I18n;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.components.ActionComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.utils.ComponentIterator;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static io.github.kaktushose.jdac.dispatching.context.internal.RichInvocationContext.*;
import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

/// Handles all the business logic for sending messages, including embeds or V1 components.
public sealed class MessageReply permits ConfigurableReply, SendableReply {

    private static final Logger log = LoggerFactory.getLogger(MessageReply.class);
    protected final ReplyAction replyAction;

    /// Constructs a new MessageReply.
    ///
    ///  @param replyConfig the [ReplyConfig] to use
    public MessageReply(ReplyConfig replyConfig) {
        replyAction = new ReplyAction(replyConfig);
    }

    /// Constructs a new MessageReply.
    ///
    ///  @param reply the [MessageReply] to copy from
    public MessageReply(MessageReply reply) {
        replyAction = reply.replyAction;
    }

    /// Acknowledgement of this event with a text message.
    ///
    /// @param message     the message to send or the localization key
    /// @param placeholder the placeholders to use to perform localization, see [I18n#localize(Locale , String, Entry...) ]
    /// @return the [Message] that got created
    /// @implSpec Internally this method must call [RestAction#complete()], thus the [Message] object can get
    /// returned directly.
    ///
    /// This might throw [RuntimeException]s if JDA fails to send the message.
    public Message reply(String message, Entry... placeholder) {
        return replyAction.reply(message, placeholder);
    }

    /// Access the underlying [MessageCreateBuilder] for configuration steps not covered by [ConfigurableReply].
    ///
    /// This method exposes the internal [MessageCreateBuilder] used by JDA-Commands. Modifying fields that
    /// are also manipulated by the Reply API, like content or embeds, may lead to unexpected behaviour.
    ///
    /// ## Example:
    /// ```
    /// event.with().builder(builder -> builder.setFiles(myFile)).reply("Hello World!");
    /// ```
    ///
    /// @param consumer the [Consumer] to access the [MessageCreateBuilder]
    /// @return this instance for fluent interface
    public SendableReply builder(Consumer<MessageCreateBuilder> consumer) {
        replyAction.builder(consumer);
        return new SendableReply(this);
    }

    /// Acknowledgement of this event with one or more [Embed]s.
    ///
    /// Resolves the [Embed]s based on the given names. See [EmbedConfig] for more information.
    ///
    /// @param embeds the name of the [Embed]s to send
    /// @return this instance for fluent interface
    public SendableReply embeds(String... embeds) {
        return embeds(Arrays.stream(embeds)
                .map(it -> getFramework().embeds().get(it, getJdaEvent().getUserLocale().toLocale()))
                .toArray(Embed[]::new));
    }

    /// Acknowledgement of this event with one or more [Embed]s.
    ///
    /// See [EmbedConfig] for more information.
    ///
    /// @param embeds the [Embed]s to send
    /// @return this instance for fluent interface
    public SendableReply embeds(Embed... embeds) {
        replyAction.addEmbeds(Arrays.stream(embeds).map(Embed::build).toArray(MessageEmbed[]::new));
        return new SendableReply(this);
    }

    /// Acknowledgement of this event with an [Embed].
    ///
    /// Resolves the [Embed] based on the given name. See [EmbedConfig] for more information.
    ///
    /// @param embed    the name of the [Embed] to send
    /// @param consumer a [Consumer] allowing direct modification of the [Embed] before sending it.
    /// @return this instance for fluent interface
    public SendableReply embeds(String embed, Consumer<Embed> consumer) {
        Embed resolved = getFramework().embeds().get(embed, getJdaEvent().getUserLocale().toLocale());
        consumer.accept(resolved);
        replyAction.addEmbeds(resolved.build());
        return new SendableReply(this);
    }

    /// Acknowledgement of this event with an [Embed].
    ///
    /// Resolves the [Embed] based on the given name. See [EmbedConfig] for more information.
    ///
    /// @param embed   the name of the [Embed] to send
    /// @param entry   the placeholders to use. See [Embed#placeholders(Entry...)]
    /// @param entries the placeholders to use. See [Embed#placeholders(Entry...)]
    /// @return this instance for fluent interface
    public SendableReply embeds(String embed, Entry entry, Entry... entries) {
        Embed resolved = getFramework().embeds().get(embed, getJdaEvent().getUserLocale().toLocale());
        resolved.placeholders(entry).placeholders(entries);
        replyAction.addEmbeds(resolved.build());
        return new SendableReply(this);
    }

    /// Adds an [ActionRow] to the reply and adds the passed components to it.
    ///
    /// The components will always be enabled and runtime-bound. Use [#components(Component...)] if you want to modify these
    /// settings.
    ///
    /// **The components must be defined in the same class where this method gets called!**
    ///
    /// ### Example:
    /// ```
    ///  @Interaction
    ///  public class ExampleCommand {
    ///
    ///     @SlashCommand(value= "example command")
    ///     public void onCommand(CommandEvent event){
    ///         event.with().components("onButton").reply("Hello World");
    ///     }
    ///
    ///     @Button("Press me!")
    ///     public void onButton(ComponentEvent event){
    ///         event.reply("You pressed me!");
    ///     }
    ///  }
    /// ```
    public SendableReply components(String... components) {
        return components(Arrays.stream(components).map(Component::enabled).toArray(Component[]::new));
    }

    /// Adds an [ActionRow] to the reply and adds the passed [Component] to it.
    ///
    /// ### Example:
    /// ```
    ///  @Interaction
    ///  public class ExampleCommand {
    ///
    ///     @SlashCommand(value= "example command")
    ///     public void onCommand(CommandEvent event){
    ///         event.with().components(Components.disabled("onButton")).reply("Hello World");
    ///     }
    ///
    ///     @Button("Press me!")
    ///     public void onButton(ComponentEvent event){
    ///         event.reply("You pressed me!");
    ///     }
    ///  }
    /// ```
    /// @see Component
    public SendableReply components(Component<?, ?, ?, ?>... components) {
        List<ActionRowChildComponent> items = Arrays.stream(components).map(it -> resolve(it, true)).toList();
        if (!items.isEmpty()) {
            replyAction.addComponents(ActionRow.of(items));
        }
        return new SendableReply(this);
    }

    protected ActionRowChildComponent resolve(Component<?, ?, ?, ?> component, boolean checkDuplicate) {
        var className = component.origin().map(Class::getName)
                .orElseGet(() -> getInvocationContext().definition().methodDescription().declaringClass().getName());
        String definitionId = InteractionDefinition.createDefinitionId(className, component.name());

        boolean duplicate = ComponentIterator.createStream(replyAction.componentTree().getComponents())
                .filter(it -> it instanceof ActionComponent)
                .map(ActionComponent.class::cast)
                .map(ActionComponent::getCustomId)
                .filter(Objects::nonNull)
                .map(CustomId::fromMerged)
                .anyMatch(customId -> customId.definitionId().equals(definitionId));
        if (duplicate && checkDuplicate) {
            throw new IllegalArgumentException(
                    JDACException.errorMessage("duplicate-component", entry("method", "%s#%s".formatted(className, component.name())))
            );
        }

        var definition = findDefinition(component, definitionId, className);

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

        item = resolve(item, component);
        log.debug("Reply Debug: Adding component \"{}\" to the reply", definition.displayName());
        return item;
    }

    private ActionRowChildComponent resolve(ActionRowChildComponent item, Component<?, ?, ?, ?> component) {
        return switch (item) {
            case Button button -> button.withLabel(resolveMessage(button.getLabel(), component));
            case EntitySelectMenu menu ->
                    menu.createCopy().setPlaceholder(orNull(menu.getPlaceholder(), p -> resolveMessage(p, component))).build();
            case StringSelectMenu menu -> {
                StringSelectMenu.Builder copy = menu.createCopy();
                List<SelectOption> localized = copy.getOptions()
                        .stream()
                        .map(option -> option.withDescription(orNull(option.getDescription(), d -> resolveMessage(d, component)))
                                .withLabel(resolveMessage(option.getLabel(), component)))
                        .toList();
                copy.getOptions().clear();
                copy.addOptions(localized);
                copy.setPlaceholder(orNull(copy.getPlaceholder(), p -> resolveMessage(p, component)));
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

    private String resolveMessage(String key, Component<?, ?, ?, ?> component) {
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

    private CustomId createId(InteractionDefinition definition, boolean independent) {
        return independent
                ? CustomId.independent(definition.definitionId())
                : new CustomId(getRuntime().id(), definition.definitionId());
    }
}
