package io.github.kaktushose.jdac.dispatching.reply;

import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.annotations.interactions.ReplyConfig;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.message.i18n.I18n;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.Locale;

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
public final class ConfigurableReply extends MessageReply {

    /// Constructs a new ConfigurableReply.
    ///
    /// @param replyConfig the underlying [InteractionDefinition.ReplyConfig]
    public ConfigurableReply(InteractionDefinition.ReplyConfig replyConfig) {
        super(replyConfig);
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
    public ConfigurableReply keepComponents(boolean keepComponents) {
        replyAction.keepComponents(keepComponents);
        return this;
    }

    /// Whether to keep the selections of a string select menu when sending edits. This setting only has an effect with
    /// [#keepComponents(boolean)] `true`.
    ///
    /// @param keepSelections `true` if to keep the selections
    /// @return the current instance for fluent interface
    public ConfigurableReply keepSelections(boolean keepSelections) {
        replyAction.keepSelections(keepSelections);
        return this;
    }

    /// Acknowledgement of this event with V2 Components.
    ///
    /// Using V2 components removes the top-level component limit,
    /// and allows more components in total ({@value Message#MAX_COMPONENT_COUNT_IN_COMPONENT_TREE}).
    ///
    /// They also allow you to use a larger choice of components, such as any component extending [MessageTopLevelComponent],
    /// as long as they are [compatible][Component.Type#isMessageCompatible()].
    ///
    /// The character limit for the messages also gets changed to {@value Message#MAX_CONTENT_LENGTH_COMPONENT_V2}.
    ///
    /// This, however, comes with a few drawbacks:
    ///
    ///   - You cannot send content, embeds, polls or stickers
    ///   - It does not support voice messages
    ///   - It does not support previewing files
    ///   - URLs don't create embeds
    ///   - You cannot switch this message back to not using Components V2 (you can however upgrade a message to V2)
    public Message reply(MessageTopLevelComponent component, MessageTopLevelComponent... components) {
        return new ComponentReplyAction(replyAction.replyConfig(), component, components).reply();
    }
}

