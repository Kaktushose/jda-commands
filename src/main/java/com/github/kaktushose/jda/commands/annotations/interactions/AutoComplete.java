package com.github.kaktushose.jda.commands.annotations.interactions;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated with AutoComplete will be registered as a handler for
 * {@link com.github.kaktushose.jda.commands.dispatching.interactions.autocomplete.AutoCompleteEvent AutoCompleteEvents}.
 *
 * @see SlashCommand
 * @since 4.0.0
 */
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
