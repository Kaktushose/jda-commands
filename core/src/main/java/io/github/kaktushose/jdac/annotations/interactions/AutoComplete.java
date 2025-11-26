package io.github.kaktushose.jdac.annotations.interactions;


import io.github.kaktushose.jdac.dispatching.events.interactions.AutoCompleteEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Methods annotated with AutoComplete will be registered as a handler for [AutoCompleteEvent]s for the given
/// [Command]s.
///
/// The [Command]s can either be referenced by:
/// 1. Command Name
///
///     If referenced by the command name the handler will handle any command thats name starts with the given name:
///     ```
///     @SlashCommand("favourite fruit")
///     public void fruitCommand(CommandEvent event, String fruit) {
///         event.reply("You've chosen: %s", fruit);
///     }
///     @SlashCommand("favourite vegetable")
///     public void vegetableCommand(CommandEvent event, String vegetable) {
///         event.reply("You've chosen: %s", vegetable);
///     }
///
///     @AutoComplete("favourite")
///     public void onFavouriteAutoComplete(AutoCompleteEvent event) {
///         event.replyChoices(...);
///     }
///     ```
/// 2. Method Name
///
///     If referenced by the method name the handler will only handle the command of the given method:
///     ```
///     @SlashCommand("favourite fruit")
///     public void fruitCommand(CommandEvent event, String fruit) {
///         event.reply("You've chosen: %s", fruit);
///     }
///
///     @AutoComplete("fruitCommand")
///     public void onFruitAutoComplete(AutoCompleteEvent event) {
///         event.replyChoices(...);
///     }
///     ```
///
/// **Be aware that the example above will register *every* command option with auto complete enabled.** If you want to
/// avoid that, you have to explicitly state the command options the handler supports:
/// ```java
/// @SlashCommand("favourite food")
/// public void foodCommand(CommandEvent event, String fruit, String vegetable) {
///     event.reply("You've chosen: %s and %s".formatted(fruit, vegetable));
/// }
///
/// @AutoComplete(vale = "foodCommand", options = "fruit")
/// public void onFruitAutoComplete(AutoCompleteEvent event) {
///     event.replyChoices(...);
/// }
/// ```
/// You can have multiple auto complete handler for the same slash command, but each command option can only have
/// exactly *one* handler.
/// If an auto complete handler doesn't specify any command options, it will be registered implicitly for every command
/// option of the given slash command(s), unless an explicit auto complete handler exists for that command option.
/// @see Command
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoComplete {

    /// Returns the name of the slash commands this autocomplete should handle.
    ///
    /// @return the slash commands
    String[] value();

    /// Returns the name of the command options this autocomplete should handle
    ///
    /// @return the command options
    String[] options() default "";

}
