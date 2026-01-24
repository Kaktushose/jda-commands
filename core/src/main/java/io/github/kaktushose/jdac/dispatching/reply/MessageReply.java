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
import io.github.kaktushose.jdac.exceptions.internal.JDACException;
import io.github.kaktushose.jdac.internal.logging.JDACLogger;
import io.github.kaktushose.jdac.message.i18n.I18n;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import io.github.kaktushose.jdac.message.resolver.ComponentResolver;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Consumer;

import static io.github.kaktushose.jdac.introspection.internal.IntrospectionAccess.*;
import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

/// Handles all the business logic for sending messages, including embeds or V1 components.
public sealed class MessageReply permits ConfigurableReply, SendableReply {

    private static final Logger log = JDACLogger.getLogger(MessageReply.class);
    protected final ReplyAction replyAction;
    private final ComponentResolver<ActionRowChildComponent> resolver;

    /// Constructs a new MessageReply.
    ///
    ///  @param replyConfig the [ReplyConfig] to use
    public MessageReply(ReplyConfig replyConfig) {
        replyAction = new ReplyAction(replyConfig);
        resolver = new ComponentResolver<>(scopedMessageResolver(), ActionRowChildComponent.class);
    }

    /// Constructs a new MessageReply.
    ///
    ///  @param reply the [MessageReply] to copy from
    public MessageReply(MessageReply reply) {
        replyAction = reply.replyAction;
        resolver = new ComponentResolver<>(scopedMessageResolver(), ActionRowChildComponent.class);
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
                .map(it -> scopedEmbeds().get(it, scopedUserLocale()))
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
        Embed resolved = scopedEmbeds().get(embed, scopedUserLocale());
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
        Embed resolved = scopedEmbeds().get(embed, scopedUserLocale());
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
        List<ActionRowChildComponent> items = Arrays.stream(components).map(this::resolve).toList();
        if (!items.isEmpty()) {
            replyAction.addComponents(ActionRow.of(items));
        }
        return new SendableReply(this);
    }

    protected ComponentReplacer resolver() {
        return ComponentReplacer.of(Component.class, _ -> true, this::resolve);
    }

    private ActionRowChildComponent resolve(Component<?, ?, ?, ?> component) {
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
