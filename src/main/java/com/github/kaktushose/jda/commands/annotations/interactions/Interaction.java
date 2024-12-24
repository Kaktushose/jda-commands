package com.github.kaktushose.jda.commands.annotations.interactions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Classes annotated with [Interaction] are responsible for defining and handling interactions.
///
/// A class annotated with [Interaction] can define interactions via its methods. Therefore, such methods must be annotated
/// with one of the following interaction annotations: [SlashCommand], [ContextCommand], [Button], [EntitySelectMenu],
/// [StringSelectMenu] or [Modal]. See the respective annotations for details.
///
/// ## Example:
/// ```
/// public class InteractionClass {
///
///     @SlashCommand("greet")
///     public void onCommand(CommandEvent event) {
///         event.reply("Hello World!");
///     }
/// }
/// ```
///
/// @since 4.0.0
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Interaction {

    /**
     * Returns the base name for slash commands.
     *
     * @return the base name for slash commands
     */
    String value() default "";

}
