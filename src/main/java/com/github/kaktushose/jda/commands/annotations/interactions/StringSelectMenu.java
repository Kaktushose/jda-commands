package com.github.kaktushose.jda.commands.annotations.interactions;

import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.dispatching.reply.Component;
import com.github.kaktushose.jda.commands.dispatching.reply.ConfigurableReply;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/// Methods annotated with StringSelectMenu will be registered as a StringSelectMenu at startup.
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
/// @SelectOption(label= "Pizza", value = "pizza")
/// @SelectOption(label= "Hamburger", value = "hamburger")
/// @SelectOption(label= "Sushi", value = "Sushi")
/// @StringSelectMenu("What's your favourite food?")
/// public void onMenu(ComponentEvent event, List<String> choices) { ... }
/// ```
/// @see Interaction
/// @see SelectOption
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StringSelectMenu {

    /// Configure the placeholder which is displayed when no selections have been made yet.
    ///
    /// @return the placeholder which is displayed when no selections have been made yet
    String value();

    /// The minimum amount of values a user has to select.
    ///
    ///Default: `1`
    ///
    /// The minimum must not exceed the amount of available options.
    ///
    /// @return the minimum amount of values a user has to select
    int minValue() default 1;

    /// The maximum amount of values a user can select.
    ///
    ///Default: `1`
    ///
    /// The maximum must not exceed the amount of available options.
    ///
    /// @return the maximum amount of values a user can select
    int maxValue() default 1;

}
