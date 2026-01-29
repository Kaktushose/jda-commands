package io.github.kaktushose.jdac.annotations.interactions;

import io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent;
import io.github.kaktushose.jdac.dispatching.reply.Component;
import io.github.kaktushose.jdac.dispatching.reply.ConfigurableReply;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/// Methods annotated with StringMenu will be registered as a StringSelectMenu at startup.
///
/// Therefore, the method must be declared inside a class that is annotated with
/// [Interaction].
/// Furthermore, the method signature has to meet the following conditions:
///
///   - First parameter must be of type [ComponentEvent]
///   - Second parameter must be of type [`List<String>`][List]
///
/// You can reply with a string select menu by calling [ConfigurableReply#components(Component...)].
///
/// ## Example:
/// ```
/// @MenuOption(label= "Pizza", value = "pizza")
/// @MenuOption(label= "Hamburger", value = "hamburger")
/// @MenuOption(label= "Sushi", value = "Sushi")
/// @StringMenu("What's your favourite food?")
/// public void onMenu(ComponentEvent event, List<String> choices) { ... }
/// ```
///
/// @see Interaction
/// @see MenuOption
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StringMenu {

    /// Configure the placeholder which is displayed when no selections have been made yet.
    ///
    /// @return the placeholder which is displayed when no selections have been made yet
    String value() default "";

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

    /// The uniqueId of this component. Must be greater than 0. Default value is `-1` which will result in Discord
    /// auto assigning an id.
    int uniqueId() default -1;
}
