package io.github.kaktushose.jdac.annotations.interactions;

import io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent;
import io.github.kaktushose.jdac.dispatching.reply.Component;
import io.github.kaktushose.jdac.dispatching.reply.ConfigurableReply;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu.DefaultValue;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu.SelectTarget;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Methods annotated with EntityMenu will be registered as an EntitySelectMenu at startup.
///
/// Therefore, the method must be declared inside a class that is annotated with
/// [Interaction].
/// Furthermore, the method signature has to meet the following conditions:
///
///   - First parameter must be of type [ComponentEvent]
///   - Second parameter must be of type [Mentions]
///
/// You can reply with an entity select menu by calling [ConfigurableReply#components(Component...)].
///
/// ## Example:
/// ```
/// @EntityMenu(value = SelectTarget.USER, placeholder = "Who's your favourite user?")
/// public void onMenu(ComponentEvent event, Mentions mentions) { ... }
/// ```
/// @see Interaction
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityMenu {

    /// Supported [SelectTarget]s.
    ///
    /// @return an array of supported [SelectTarget]s
    SelectTarget[] value();

    /// The [default values][DefaultValue]
    /// for roles that will be shown to the user.
    ///
    /// @return the [default values][DefaultValue]
    long[] defaultRoles() default -1;

    /// The [default values][DefaultValue]
    /// for channels that will be shown to the user.
    ///
    /// @return the [default values][DefaultValue]
    long[] defaultChannels() default -1;

    /// The [default values][DefaultValue]
    /// for users that will be shown to the user.
    ///
    /// @return the [default values][DefaultValue]
    long[] defaultUsers() default -1;

    /// The [ChannelType]s that should be supported by this menu.
    /// This is only relevant for menus that allow CHANNEL targets.
    ///
    /// @return the [ChannelType]s that should be supported by this menu
    ChannelType[] channelTypes() default ChannelType.UNKNOWN;

    /// Configure the placeholder which is displayed when no selections have been made yet.
    ///
    /// @return the placeholder which is displayed when no selections have been made yet
    String placeholder() default "";

    /// The minimum amount of values a user has to select.
    ///
    /// Default: `1`
    ///
    /// The minimum must not exceed the amount of available options.
    ///
    /// @return the minimum amount of values a user has to select
    int minValue() default 1;

    /// The maximum amount of values a user can select.
    ///
    /// Default: `1`
    ///
    /// The maximum must not exceed the amount of available options.
    ///
    /// @return the maximum amount of values a user can select
    int maxValue() default 1;

    /// The uniqueId of this component. Must be greater than 0. Default value is `-1` which will result in Discord auto assigning an id.
    int uniqueId() default -1;
}
