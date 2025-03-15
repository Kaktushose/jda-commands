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
import com.github.kaktushose.jda.commands.dispatching.reply.component.ButtonComponent;
import com.github.kaktushose.jda.commands.dispatching.reply.component.EntitySelectMenuComponent;
import com.github.kaktushose.jda.commands.dispatching.reply.component.StringSelectComponent;
import com.github.kaktushose.jda.commands.dispatching.reply.component.UnspecificComponent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

/// Subtype of [MessageReply] that supports adding components to messages and changing the [ReplyConfig].
///
/// ### Example:
/// ```
/// @Interaction
/// public class ExampleCommand {
///
///     @SlashCommand(value = "example command")
///     public void onCommand(CommandEvent event) {
///         event.with().components(buttons("onButton")).reply("Hello World");
///     }
///
///     @Button("Press me!")
///     public void onButton(ComponentEvent event) {
///         event.reply("You pressed me!");
///     }
/// }
/// ```
public sealed class ConfigurableReply extends MessageReply permits ComponentReply {

    protected final InteractionRegistry registry;
    protected final String runtimeId;

    /// Constructs a new ConfigurableReply.
    ///
    /// @param reply     the underlying [MessageReply]
    /// @param registry  the corresponding [InteractionRegistry]
    /// @param runtimeId the corresponding [Runtime]
    public ConfigurableReply(@NotNull MessageReply reply, @NotNull InteractionRegistry registry, @NotNull String runtimeId) {
        super(reply);
        this.registry = registry;
        this.runtimeId = runtimeId;
    }

    /// Constructs a new ConfigurableReply.
    ///
    /// @param reply the [ConfigurableReply] to copy
    public ConfigurableReply(@NotNull ConfigurableReply reply) {
        super(reply);
        this.registry = reply.registry;
        this.runtimeId = reply.runtimeId;
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
        this.ephemeral = ephemeral;
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
        this.editReply = editReply;
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
        this.keepComponents = keepComponents;
        return this;
    }

    /// Access the underlying [MessageCreateBuilder] for configuration steps not covered by [ConfigurableReply].
    ///
    /// ## Example:
    /// ```
    /// event.with().builder(builder -> builder.setFiles(myFile)).reply("Hello World!");
    /// ```
    ///
    /// @param builder the [MessageCreateBuilder] callback
    /// @return the current instance for fluent interface
    public ConfigurableReply builder(Consumer<MessageCreateBuilder> builder) {
        builder.accept(this.builder);
        return this;
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
    /// @Interaction
    /// public class ExampleCommand {
    ///
    ///     @SlashCommand(value = "example command")
    ///     public void onCommand(CommandEvent event) {
    ///         event.with().components("onButton").reply("Hello World");
    ///     }
    ///
    ///     @Button("Press me!")
    ///     public void onButton(ComponentEvent event) {
    ///         event.reply("You pressed me!");
    ///     }
    /// }
    /// ```
    /// @param components the name of the components to add
    /// @return the current instance for fluent interface
    @NotNull
    public ComponentReply components(@NotNull String... components) {
        return components(Arrays.stream(components).map(Component::enabled).toArray(Component[]::new));
    }

    /// Adds an [ActionRow] to the reply and adds the passed [Component] to it.
    ///
    /// **The components must be defined in the same class where this method gets called!**
    ///
    /// ### Example:
    /// ```
    /// @Interaction
    /// public class ExampleCommand {
    ///
    ///     @SlashCommand(value = "example command")
    ///     public void onCommand(CommandEvent event) {
    ///         event.with().components(Components.disabled("onButton")).reply("Hello World");
    ///     }
    ///
    ///     @Button("Press me!")
    ///     public void onButton(ComponentEvent event) {
    ///         event.reply("You pressed me!");
    ///     }
    /// }
    /// ```
    /// @see Component
    /// @param components the [Component] to add
    /// @return the current instance for fluent interface
    @NotNull
    public final ComponentReply components(@NotNull Component<?, ?, ?>... components) {
        List<ItemComponent> items = new ArrayList<>();
        for (Component<?, ?, ?> component : components) {
            var className = component.origin() == null
                    ? definition.methodDescription().declaringClass().getName()
                    : component.origin().getName();
            String definitionId = String.valueOf((className + component.name()).hashCode());

            if (builder.getComponents()
                    .stream()
                    .flatMap(itemComponents -> itemComponents.getActionComponents().stream())
                    .map(ActionComponent::getId)
                    .filter(Objects::nonNull)
                    .map(CustomId::fromMerged)
                    .anyMatch(customId -> customId.definitionId().equals(definitionId))) {
                throw new IllegalArgumentException("Cannot add component %s#%s multiple times!".formatted(className, component.name()));
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
                case EntitySelectMenuComponent entitySelectMenuComponent -> entitySelectMenuComponent.callback().apply((EntitySelectMenu) item);
                case StringSelectComponent stringSelectComponent -> stringSelectComponent.callback().apply((StringSelectMenu) item);
                case UnspecificComponent unspecificComponent -> unspecificComponent.callback().apply(item);
            };

            items.add(item);

            log.debug("Reply Debug: Adding component \"{}\" to the reply", definition.displayName());
        }

        if (!items.isEmpty()) {
            builder.addComponents(ActionRow.of(items));
        }

        return new ComponentReply(this);
    }

    private <D extends ComponentDefinition<?>, T extends Component<T, ?, D>> D findDefinition(Component<T, ?, D> component, String definitionId) {
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

