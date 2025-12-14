package io.github.kaktushose.jdac.dispatching.reply;

import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.annotations.interactions.ReplyConfig;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.tree.ComponentTree;
import net.dv8tion.jda.api.components.tree.MessageComponentTree;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

import java.util.Arrays;
import java.util.stream.Stream;

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
        MessageComponentTree componentTree = ComponentTree.forMessage(Stream.concat(Stream.of(component), Arrays.stream(components)).toList());
        componentTree = componentTree.replace(ComponentReplacer.of(
                io.github.kaktushose.jdac.dispatching.reply.Component.class,
                _ -> true,
                it -> resolve(it, true)
        ));
        return replyAction.reply(componentTree.getComponents());
    }
}

