package io.github.kaktushose.jdac.dispatching.reply;

import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.annotations.interactions.ReplyConfig;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import io.github.kaktushose.jdac.message.placeholder.PlaceholderResolver;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.tree.ComponentTree;
import net.dv8tion.jda.api.components.tree.MessageComponentTree;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateRequest;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
public sealed class ConfigurableReply extends MessageReply permits EditableConfigurableReply {

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

    /// Whether to suppress notifications of this message. Defaults to `false`.
    ///
    /// **This will override both [JDACBuilder#globalReplyConfig(InteractionDefinition.ReplyConfig)] and any [ReplyConfig] annotation!**
    ///
    /// @return the current instance for fluent interface
    /// @see MessageCreateRequest#setSuppressedNotifications(boolean)
    public ConfigurableReply silent(boolean silent) {
        replyAction.silent(silent);
        return this;
    }

    /// Sets the [MentionType]s to be parsed.
    ///
    /// **This will override [JDACBuilder#globalReplyConfig(InteractionDefinition.ReplyConfig)], any [ReplyConfig] annotation
    /// as well as previous method calls!**
    ///
    /// If `null` is provided to this method, then all Types will be mentionable (unless whitelisting via
    /// [#mention(IMentionable...)] or [#mention(Collection)]).
    ///
    /// @param allowedMentions [MentionType]s that are allowed to be parsed and mentioned. All other mention types will
    ///                        not be mentioned by this message. You can pass `null` or
    ///                        `EnumSet.allOf(MentionType.class)` to allow all mentions.
    /// @return the current instance for fluent interface
    /// @see MessageCreateRequest#setAllowedMentions(Collection)
    public ConfigurableReply allowedMentions(@Nullable Collection<MentionType> allowedMentions) {
        replyAction.allowedMentions(allowedMentions);
        return this;
    }

    /// Used to provide a whitelist for Users, Members and Roles that should be pinged. See the JDA docs for details.
    ///
    /// This will add up to previous method calls.
    ///
    /// @param mentions Users, Members and Roles that should be explicitly whitelisted to be pingable.
    /// @return the current instance for fluent interface
    /// @see MessageCreateRequest#mention(IMentionable...)
    public ConfigurableReply mention(IMentionable... mentions) {
        return mention(Arrays.asList(mentions));
    }

    /// Used to provide a whitelist for Users, Members and Roles that should be pinged. See the JDA docs for details.
    ///
    /// This will add up to previous method calls.
    ///
    /// @param mentions Users, Members and Roles that should be explicitly whitelisted to be pingable.
    /// @return the current instance for fluent interface
    /// @see MessageCreateRequest#mention(Collection)
    public ConfigurableReply mention(Collection<IMentionable> mentions) {
        replyAction.mention(mentions);
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
    ///
    /// @param component   the [MessageTopLevelComponent] to reply with
    /// @param placeholder the [placeholders][Entry] to use. See [PlaceholderResolver]
    public Message reply(MessageTopLevelComponent component, Entry... placeholder) {
        return reply(List.of(component), placeholder);
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
    ///
    /// @param components  a [Collection] of [MessageTopLevelComponent]s to reply with
    /// @param placeholder the [placeholders][Entry] to use. See [PlaceholderResolver]
    public Message reply(Collection<MessageTopLevelComponent> components, Entry... placeholder) {
        MessageComponentTree componentTree = ComponentTree.forMessage(components);
        componentTree = componentTree.replace(resolver());
        return replyAction.reply(componentTree.getComponents(), placeholder);
    }
}

