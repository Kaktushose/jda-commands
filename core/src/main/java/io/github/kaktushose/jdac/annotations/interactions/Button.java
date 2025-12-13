package io.github.kaktushose.jdac.annotations.interactions;

import io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent;
import io.github.kaktushose.jdac.dispatching.reply.Component;
import io.github.kaktushose.jdac.dispatching.reply.ConfigurableReply;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Methods annotated with [Button] will be registered as a button at startup.
///
/// Therefore, the method must be declared inside a class that is annotated with
/// [Interaction].
/// Furthermore, the method signature has to meet the following conditions:
///
///   - First parameter must be of type [ComponentEvent]
///
/// You can reply with a button by calling [ConfigurableReply#components(Component...)].
///
/// ## Example:
/// ```
/// @Button(value = "Press me", style = ButtonStyle.DANGER)
/// public void onButton(ComponentEvent event) { ... }
/// ```
///
/// @see Interaction
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Button {

    /// Gets the label of the button.
    ///
    /// @return the label of the button
    String value() default "";

    /// Gets the [ButtonStyle].
    ///
    /// @return the [ButtonStyle]
    ButtonStyle style() default ButtonStyle.PRIMARY;

    /// Gets the [Emoji] of the button.
    ///
    /// @return the [Emoji]
    String emoji() default "";

    /// Gets the link of the button.
    ///
    /// @return the link of the button
    String link() default "";

}
