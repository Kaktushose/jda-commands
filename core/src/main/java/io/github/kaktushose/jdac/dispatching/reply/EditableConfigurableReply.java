package io.github.kaktushose.jdac.dispatching.reply;

import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.annotations.interactions.ReplyConfig;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.exceptions.internal.JDACException;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;

/// Subtype of [ConfigurableReply] that is used for [ComponentInteractions][net.dv8tion.jda.api.interactions.components.ComponentInteraction],
/// where you can also edit the original reply instead of sending a new one.
public final class EditableConfigurableReply extends ConfigurableReply {

    private final ComponentInteraction interaction;

    /// Constructs a new ConfigurableReply.
    ///
    /// @param replyConfig the underlying [InteractionDefinition.ReplyConfig]
    public EditableConfigurableReply(InteractionDefinition.ReplyConfig replyConfig, ComponentInteraction interaction) {
        super(replyConfig);
        this.interaction = interaction;
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
    /// @param editReply `true` if to keep the original components
    /// @return the current instance for fluent interface
    public EditableConfigurableReply editReply(boolean editReply) {
        replyAction.editReply(editReply);
        return this;
    }

    /// Whether to keep the original components when editing a message. Default value is `true`.
    ///
    /// More formally, if editing a message and `keepComponents` is `true`, the original message will first be queried and
    /// its components get added to the reply before it is sent.
    ///
    /// **This will override both [JDACBuilder#globalReplyConfig(InteractionDefinition.ReplyConfig)] and any [ReplyConfig] annotation!**
    ///
    /// @param keepComponents `true` if to edit the original method
    /// @return the current instance for fluent interface
    public EditableConfigurableReply keepComponents(boolean keepComponents) {
        replyAction.keepComponents(keepComponents);
        return this;
    }

    /// Whether to keep the selections of a string select menu when sending edits. This setting only has an effect with
    /// [#keepComponents(boolean)] `true`.
    ///
    /// @param keepSelections `true` if to keep the selections
    /// @return the current instance for fluent interface
    public EditableConfigurableReply keepSelections(boolean keepSelections) {
        replyAction.keepSelections(keepSelections);
        return this;
    }

    /// Acknowledgement of this event with the V2 Components of the original reply. Will also apply the passed
    /// [ComponentReplacer] before sending the reply.
    ///
    /// This method will always set [#keepComponents(boolean)] to `true` to retrieve the original components.
    ///
    /// @param replacer the [ComponentReplacer] to apply to the original components
    /// @throws UnsupportedOperationException if the original message didn't use V2 Components
    public Message reply(ComponentReplacer... replacer) {
        if (!interaction.getMessage().isUsingComponentsV2()) {
            throw new UnsupportedOperationException(JDACException.errorMessage("component-replacer-v1"));
        }

        replyAction.keepComponents(true);

        return replyAction.reply(replacer);
    }

}
