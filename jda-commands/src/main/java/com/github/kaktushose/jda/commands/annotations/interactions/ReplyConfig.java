package com.github.kaktushose.jda.commands.annotations.interactions;

import com.github.kaktushose.jda.commands.JDACommandsBuilder;
import com.github.kaktushose.jda.commands.dispatching.events.ReplyableEvent;
import com.github.kaktushose.jda.commands.dispatching.reply.ConfigurableReply;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Used to configure the reply behaviour of interaction replies.
///
/// Interaction methods annotated with [ReplyConfig] will use the configured values of this annotation when sending a reply.
/// Interaction classes annotated with [ReplyConfig] will apply the configured values of this annotation to
/// every method, if and only if no annotation is present at method level. If the [ReplyConfig] annotation is neither
/// present at the class level nor the method level, the global [`ReplyConfig`][com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition.ReplyConfig]
///  will be used instead.
///
/// **These values are always overridden by [ConfigurableReply#ephemeral(boolean)],
/// [ConfigurableReply#keepComponents(boolean)] or respectively [ConfigurableReply#editReply(boolean)].**
///
/// In other words the hierarchy is as following:
/// 1. [ConfigurableReply]
/// 2. [ReplyConfig] method annotation
/// 3. [ReplyConfig] class annotation
/// 4. global [`ReplyConfig`][com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition.ReplyConfig] provided in [JDACommandsBuilder]
///
/// @see ReplyableEvent
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReplyConfig {

    /// Whether to send ephemeral replies. Default value is `false`.
    ///
    /// Ephemeral messages have some limitations and will be removed once the user restarts their client.
    /// Limitations:
    /// - Cannot contain any files/ attachments
    /// - Cannot be reacted to
    /// - Cannot be retrieved
    ///
    /// @return `true` if to send ephemeral replies
    boolean ephemeral() default false;

    /// Whether to keep the original components when editing a message. Default value is `true`.
    ///
    /// More formally, if editing a message and `keepComponents` is `true`, the original message will first be queried and
    /// its components get added to the reply before it is sent.
    ///
    /// @return `true` if to keep the original components
    boolean keepComponents() default true;

    /// Whether to edit the original message or to send a new one. Default value is `true`.
    ///
    /// The original message is the message, from which this event (interaction) originates.
    /// For example if this event is a ButtonEvent, the original message will be the message to which the pressed button is attached to.
    ///
    /// Subsequent replies to the same slash command event or the button event cannot be edited.
    ///
    /// @return `true` if to edit the original method
    boolean editReply() default true;

}
