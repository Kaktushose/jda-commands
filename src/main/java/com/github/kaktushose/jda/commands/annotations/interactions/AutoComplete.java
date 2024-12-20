package com.github.kaktushose.jda.commands.annotations.interactions;


import com.github.kaktushose.jda.commands.dispatching.events.interactions.AutoCompleteEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Methods annotated with AutoComplete will be registered as a handler for [AutoCompleteEvent]s for the given
/// [SlashCommand](s).
///
/// ## Example:
/// ```
/// @SlashCommand(value = "favourite fruit")
/// public void fruitCommand(CommandEvent event, String fruit) {
///     event.reply("You've chosen: %s", fruit);
/// }
///
/// @AutoComplete("fruitCommand")
/// public void onFruitAutoComplete(AutoCompleteEvent event) {
///     event.replyChoices(...);
/// }
/// ```
///
/// @see SlashCommand
/// @since 4.0.0
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoComplete {

    /**
     * Returns the name of the slash commands this autocomplete should handle.
     *
     * @return the slash commands
     */
    String[] value();

}
