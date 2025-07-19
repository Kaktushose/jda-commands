package com.github.kaktushose.jda.commands.dispatching.reply;

import com.github.kaktushose.jda.commands.JDACBuilder;
import com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig;
import com.github.kaktushose.jda.commands.definitions.interactions.CustomId;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ButtonDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ComponentDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.SelectMenuDefinition;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.reply.dynamic.ButtonComponent;
import com.github.kaktushose.jda.commands.dispatching.reply.dynamic.internal.UnspecificComponent;
import com.github.kaktushose.jda.commands.dispatching.reply.dynamic.menu.EntitySelectMenuComponent;
import com.github.kaktushose.jda.commands.dispatching.reply.dynamic.menu.StringSelectComponent;
import com.github.kaktushose.jda.commands.dispatching.reply.internal.ReplyAction;
import com.github.kaktushose.jda.commands.embeds.Embed;
import com.github.kaktushose.jda.commands.embeds.EmbedConfig;
import com.github.kaktushose.jda.commands.embeds.internal.Embeds;
import com.github.kaktushose.jda.commands.i18n.I18n;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

/// Builder for sending messages based on a [GenericInteractionCreateEvent] that supports adding components to
/// messages and changing the [InteractionDefinition.ReplyConfig].
///
/// ### Example:
/// ```
/// @Interaction
/// public class ExampleCommand {
///
///     @SlashCommand(value= "example command")
///     public void onCommand(CommandEvent event){
///         event.with().components(buttons("onButton")).reply("Hello World");
///     }
///
///     @Button("Press me!")
///     public void onButton(ComponentEvent event){
///         event.reply("You pressed me!");
///     }
/// }
/// ```
public sealed class ConfigurableReply permits SendableReply {

    private static final Logger log = LoggerFactory.getLogger(ConfigurableReply.class);
    protected final ReplyAction replyAction;
    private final GenericInteractionCreateEvent event;
    private final InteractionDefinition definition;
    private final I18n i18n;
    private final Embeds embeds;
    private final InteractionRegistry registry;
    private final String runtimeId;


    /// Constructs a new ConfigurableReply.
    ///
    /// @param event       the [GenericInteractionCreateEvent] that should be responded to
    /// @param definition  the [InteractionDefinition] belonging to the event
    /// @param i18n        the corresponding [I18n] instance
    /// @param replyAction the underlying [ReplyAction]
    /// @param embeds      the corresponding [Embeds] instance
    /// @param registry    the corresponding [InteractionRegistry]
    /// @param runtimeId   the corresponding [Runtime]
    public ConfigurableReply(@NotNull GenericInteractionCreateEvent event,
                             @NotNull InteractionDefinition definition,
                             @NotNull I18n i18n,
                             @NotNull ReplyAction replyAction,
                             @NotNull Embeds embeds,
                             @NotNull InteractionRegistry registry,
                             @NotNull String runtimeId) {
        this.event = event;
        this.definition = definition;
        this.i18n = i18n;
        this.replyAction = replyAction;
        this.embeds = embeds;
        this.registry = registry;
        this.runtimeId = runtimeId;
    }

    /// Constructs a new ConfigurableReply.
    ///
    /// @param configurableReply the [ConfigurableReply] to copy
    public ConfigurableReply(@NotNull ConfigurableReply configurableReply) {
        this.event = configurableReply.event;
        this.definition = configurableReply.definition;
        this.i18n = configurableReply.i18n;
        this.replyAction = configurableReply.replyAction;
        this.embeds = configurableReply.embeds;
        this.registry = configurableReply.registry;
        this.runtimeId = configurableReply.runtimeId;
    }

    /// Whether to send ephemeral replies. Default value is `false`.
    ///
    /// Ephemeral messages have some limitations and will be removed once the user restarts their client.
    /// Limitations:
    /// - Cannot contain any files/ attachments
    /// - Cannot be reacted to
    /// - Cannot be retrieved
    ///
    /// **This will override both [JDACBuilder#globalReplyConfig(InteractionDefinition.ReplyConfig)] and any [ReplyConfig] annotation!**
    ///
    /// @param ephemeral `true` if to send ephemeral replies
    /// @return the current instance for fluent interface
    @NotNull
    public ConfigurableReply ephemeral(boolean ephemeral) {
        replyAction.ephemeral(ephemeral);
        return this;
    }

    /// Whether to keep the original components when editing a message. Default value is `true`.
    ///
    /// More formally, if editing a message and `keepComponents` is `true`, the original message will first be queried and
    /// its components get added to the reply before it is sent.
    ///
    /// **This will override both [JDACBuilder#globalReplyConfig(InteractionDefinition.ReplyConfig)] and any [ReplyConfig] annotation!**
    ///
    /// @param editReply `true` if to keep the original components
    /// @return the current instance for fluent interface
    @NotNull
    public ConfigurableReply editReply(boolean editReply) {
        replyAction.editReply(editReply);
        return this;
    }

    /// Whether to edit the original message or to send a new one. Default value is `true`.
    ///
    /// The original message is always the very first reply that was sent. E.g. for a slash command event, which was
    /// replied to with a text message and a button, the original message is that very reply.
    ///
    /// Subsequent replies to the same slash command event or the button event cannot be edited.
    ///
    /// **This will override both [JDACBuilder#globalReplyConfig(InteractionDefinition.ReplyConfig)] and any [ReplyConfig] annotation!**
    ///
    /// @param keepComponents `true` if to edit the original method
    /// @return the current instance for fluent interface
    @NotNull
    public ConfigurableReply keepComponents(boolean keepComponents) {
        replyAction.keepComponents(keepComponents);
        return this;
    }

    /// Whether to keep the selections of a string select menu when sending edits. This setting only has an effect with
    /// [#keepComponents(boolean)] `true`.
    @NotNull
    public ConfigurableReply keepSelections(boolean keepSelections) {
        replyAction.keepSelections(keepSelections);
        return this;
    }

    /// Access the underlying [MessageCreateBuilder] for configuration steps not covered by [ConfigurableReply].
    ///
    /// This method exposes the internal [MessageCreateBuilder] used by JDA-Commands. Modifying fields that
    /// are also manipulated by the Reply API, like content or embeds, may lead to unexpected behaviour.
    ///
    /// ## Example:
    /// ```
    /// event.with().builder(builder -> builder.setFiles(myFile)).reply("Hello World!");
    ///```
    public ConfigurableReply builder(Consumer<MessageCreateBuilder> builder) {
        replyAction.builder(builder);
        return this;
    }

    /// Acknowledgement of this event with a text message.
    ///
    /// @param message     the message to send or the localization key
    /// @param placeholder the placeholders to use to perform localization, see [I18n#localize(Locale, String, I18n.Entry...)]
    /// @return the [Message] that got created
    /// @implSpec Internally this method must call [RestAction#complete()], thus the [Message] object can get
    /// returned directly.
    ///
    /// This might throw [RuntimeException]s if JDA fails to send the message.
    public Message reply(@NotNull String message, I18n.Entry... placeholder) {
        return replyAction.reply(message, placeholder);
    }

    /// Acknowledgement of this event with one or more [Embed]s.
    ///
    /// Resolves the [Embed]s based on the given names. See [EmbedConfig] for more information.
    ///
    /// @param embeds the name of the [Embed]s to send
    /// @return a new [SendableReply]
    @NotNull
    public SendableReply embeds(String... embeds) {
        return embeds(Arrays.stream(embeds).map(it -> this.embeds.get(it, event.getUserLocale().toLocale())).toArray(Embed[]::new));
    }

    /// Acknowledgement of this event with one or more [Embed]s.
    ///
    /// See [EmbedConfig] for more information.
    ///
    /// @param embeds the [Embed]s to send
    /// @return a new [SendableReply]
    @NotNull
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
    /// @return a new [SendableReply]
    @NotNull
    public SendableReply embeds(String embed, Consumer<Embed> consumer) {
        Embed resolved = embeds.get(embed, event.getUserLocale().toLocale());
        consumer.accept(resolved);
        replyAction.addEmbeds(resolved.build());
        return new SendableReply(this);
    }

    /// Acknowledgement of this event with an [Embed].
    ///
    /// Resolves the [Embed] based on the given name. See [EmbedConfig] for more information.
    ///
    /// @param embed   the name of the [Embed] to send
    /// @param entries the placeholders to use. See [Embed#placeholders(I18n.Entry...)]
    /// @return a new [SendableReply]
    @NotNull
    public SendableReply embeds(String embed, I18n.Entry... entries) {
        Embed resolved = embeds.get(embed, event.getUserLocale().toLocale());
        resolved.placeholders(entries);
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
    ///     @Button("Pressme!")
    ///     public void onButton(ComponentEvent event){
    ///         event.reply("You pressed me!");
    ///     }
    ///  }
    ///```
    /// @param components the name of the components to add
    /// @return the current instance for fluent interface
    @NotNull
    public SendableReply components(@NotNull String... components) {
        return components(Arrays.stream(components).map(Component::enabled).toArray(Component[]::new));
    }

    /// Adds an [ActionRow] to the reply and adds the passed [Component] to it.
    ///
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
    ///```
    /// @see Component
    /// @param components the [Component] to add
    /// @return the current instance for fluent interface
    @NotNull
    public SendableReply components(@NotNull Component<?, ?, ?, ?>... components) {
        List<ItemComponent> items = new ArrayList<>();
        for (Component<?, ?, ?, ?> component : components) {
            var className = component.origin().map(Class::getName)
                    .orElseGet(() -> definition.methodDescription().declaringClass().getName());
            String definitionId = String.valueOf((className + component.name()).hashCode());

            if (replyAction.components()
                    .stream()
                    .flatMap(itemComponents -> itemComponents.getActionComponents().stream())
                    .map(ActionComponent::getId)
                    .filter(Objects::nonNull)
                    .map(CustomId::fromMerged)
                    .anyMatch(customId -> customId.definitionId().equals(definitionId))) {
                throw new IllegalArgumentException("Cannot add component \"%s.%s\" multiple times!".formatted(className, component.name()));
            }

            var definition = findDefinition(component, definitionId);

            ActionComponent item = switch (definition) {
                case ButtonDefinition buttonDefinition -> {
                    var button = buttonDefinition.toJDAEntity().withDisabled(!component.enabled());
                    //only assign ids to non-link buttons
                    yield button.getUrl() == null ? button.withId(createId(definition, component.independent()).merged()) : button;
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

            item = localize(item, component);

            items.add(item);

            log.debug("Reply Debug: Adding component \"{}\" to the reply", definition.displayName());
        }

        if (!items.isEmpty()) {
            replyAction.addComponents(ActionRow.of(items));
        }
        return new SendableReply(this);
    }

    private ActionComponent localize(ActionComponent item, Component<?, ?, ?, ?> component) {
        return switch (item) {
            case Button button -> button.withLabel(localize(button.getLabel(), component));
            case EntitySelectMenu menu -> {
                menu.createCopy().setPlaceholder(localize(menu.getPlaceholder(), component));
                yield menu;
            }
            case StringSelectMenu menu -> {
                StringSelectMenu.Builder copy = menu.createCopy();
                List<SelectOption> localized = copy.getOptions()
                        .stream()
                        .map(option -> option.withDescription(localize(option.getDescription(), component))
                                .withLabel(localize(option.getLabel(), component)))
                        .toList();
                copy.getOptions().clear();
                copy.addOptions(localized);
                copy.setPlaceholder(localize(copy.getPlaceholder(), component));
                yield copy.build();
            }
            default -> throw new IllegalArgumentException("Should never occur, report this to the devs of JDA-Commands!");
        };
    }

    private String localize(String key, Component<?, ?, ?, ?> component) {
        return i18n.localize(event.getUserLocale().toLocale(), key, component.placeholder());
    }

    private <D extends ComponentDefinition<?>, T extends Component<T, ?, ?, D>> D findDefinition(Component<T, ?, ?, D> component, String definitionId) {
        // this cast is effective safe
        D definition = registry.find(component.definitionClass(), false, it ->
                it.definitionId().equals(definitionId)
        );

        return component.build(definition);

    }

    private CustomId createId(InteractionDefinition definition, boolean staticComponent) {
        return staticComponent
                ? CustomId.independent(definition.definitionId())
                : new CustomId(runtimeId, definition.definitionId());
    }
}

