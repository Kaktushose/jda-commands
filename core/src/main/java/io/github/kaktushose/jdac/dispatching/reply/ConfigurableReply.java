package io.github.kaktushose.jdac.dispatching.reply;

import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.annotations.interactions.ReplyConfig;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.dispatching.reply.internal.ComponentReplyAction;
import io.github.kaktushose.jdac.dispatching.reply.internal.MessageReplyAction;
import io.github.kaktushose.jdac.embeds.Embed;
import io.github.kaktushose.jdac.embeds.EmbedConfig;
import io.github.kaktushose.jdac.message.i18n.I18n;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;

import static io.github.kaktushose.jdac.dispatching.context.internal.RichInvocationContext.*;

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
public final class ConfigurableReply {

    private static final Logger log = LoggerFactory.getLogger(ConfigurableReply.class);
    private boolean ephemeral;
    private boolean editReply;
    private boolean keepComponents;
    private boolean keepSelections;

    /// Constructs a new ConfigurableReply.
    ///
    /// @param replyConfig the underlying [InteractionDefinition.ReplyConfig]
    public ConfigurableReply(InteractionDefinition.ReplyConfig replyConfig) {
        ephemeral = replyConfig.ephemeral();
        editReply = replyConfig.editReply();
        keepComponents = replyConfig.keepComponents();
        keepSelections = replyConfig.keepSelections();
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
    public ConfigurableReply keepComponents(boolean keepComponents) {
        this.keepComponents = keepComponents;
        return this;
    }

    /// Whether to keep the selections of a string select menu when sending edits. This setting only has an effect with
    /// [#keepComponents(boolean)] `true`.
    public ConfigurableReply keepSelections(boolean keepSelections) {
        this.keepSelections = keepSelections;
        return this;
    }

    /// Acknowledgement of this event with a text message.
    ///
    /// @param message     the message to send or the localization key
    /// @param placeholder the placeholders to use to perform localization, see [I18n#localize(Locale, String, Entry...)]
    /// @return the [Message] that got created
    /// @implSpec Internally this method must call [RestAction#complete()], thus the [Message] object can get
    /// returned directly.
    ///
    /// This might throw [RuntimeException]s if JDA fails to send the message.
    public Message reply(String message, Entry... placeholder) {
        return new MessageReplyAction(replyConfig()).reply(message, placeholder);
    }

    public Message reply(MessageTopLevelComponent component, MessageTopLevelComponent... components) {
        return new ComponentReplyAction(getReplyConfig(), component, components).reply();
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
    public MessageReply builder(Consumer<MessageCreateBuilder> consumer) {
        return newReply().builder(consumer);
    }

    /// Acknowledgement of this event with one or more [Embed]s.
    ///
    /// Resolves the [Embed]s based on the given names. See [EmbedConfig] for more information.
    ///
    /// @param embeds the name of the [Embed]s to send
    /// @return a new [SendableReply]
    public MessageReply embeds(String... embeds) {
        return embeds(Arrays.stream(embeds)
                .map(it -> getFramework().embeds().get(it, getJdaEvent().getUserLocale().toLocale()))
                .toArray(Embed[]::new));
    }

    /// Acknowledgement of this event with one or more [Embed]s.
    ///
    /// See [EmbedConfig] for more information.
    ///
    /// @param embeds the [Embed]s to send
    /// @return a new [SendableReply]
    public MessageReply embeds(Embed... embeds) {
        return newReply().embeds(embeds);
    }

    /// Acknowledgement of this event with an [Embed].
    ///
    /// Resolves the [Embed] based on the given name. See [EmbedConfig] for more information.
    ///
    /// @param embed    the name of the [Embed] to send
    /// @param consumer a [Consumer] allowing direct modification of the [Embed] before sending it.
    /// @return a new [SendableReply]
    public MessageReply embeds(String embed, Consumer<Embed> consumer) {
        return newReply().embeds(embed, consumer);
    }

    /// Acknowledgement of this event with an [Embed].
    ///
    /// Resolves the [Embed] based on the given name. See [EmbedConfig] for more information.
    ///
    /// @param embed   the name of the [Embed] to send
    /// @param entry   the placeholders to use. See [Embed#placeholders(Entry...)]
    /// @param entries the placeholders to use. See [Embed#placeholders(Entry...)]
    /// @return a new [SendableReply]
    public MessageReply embeds(String embed, Entry entry, Entry... entries) {
        return newReply().embeds(embed, entry, entries);
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
    ///```
    public MessageReply components(String... components) {
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
    ///```
    /// @see Component
    public MessageReply components(Component<?, ?, ?, ?>... components) {
        return newReply().components(components);
    }

    private InteractionDefinition.ReplyConfig replyConfig() {
        log.debug("Reply Debug: [Runtime={}]", getRuntime().id());
        return new InteractionDefinition.ReplyConfig(ephemeral, editReply, keepComponents, keepSelections);
    }

    private MessageReply newReply() {
        return new MessageReply(replyConfig());
    }
}

