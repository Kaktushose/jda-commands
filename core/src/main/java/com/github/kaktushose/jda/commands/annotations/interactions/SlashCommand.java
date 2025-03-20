package com.github.kaktushose.jda.commands.annotations.interactions;

import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapters;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Methods annotated with SlashCommand will be registered as a slash command at startup.
///
/// Therefore, the method must be declared inside a class that is annotated with [Interaction].
/// Furthermore, the method signature has to meet the following conditions:
///
///   - First parameter must be of type [CommandEvent]
///   - Remaining parameter types must be registered at the [TypeAdapters]
///
/// ## Examples:
/// ```
/// @SlashCommand("greet")
/// public void onCommand(CommandEvent event) {
///     event.reply("Hello World!");
/// }
///
/// @SlashCommand(value="moderation ban", desc="Bans a member", enabledFor=Permission.BAN_MEMBERS)
/// public void onCommand(CommandEvent event, @Param("The member to ban") Member target, @Optional("no reason given") String reason) { ... }
///
/// @SlashCommand(value = "favourite fruit")
/// public void onCommand(CommandEvent event, @Choices({"Apple", "Banana", "Orange"}) String fruit) {
///     event.reply("You've chosen: %s", fruit);
/// }
///
/// @SlashCommand("example command") {
/// public void onCommand(CommandEvent event, String[] arguments) {}
/// }
/// ```
/// @see Interaction
/// @see com.github.kaktushose.jda.commands.annotations.interactions.Interaction Interaction
/// @see com.github.kaktushose.jda.commands.annotations.constraints.Constraint Constraint
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SlashCommand {

    /// Returns the name of the command.
    ///
    /// @return the name of the command
    String value() default "";

    /// Returns the description of the command.
    ///
    /// @return the description of the command
    String desc() default "N/A";

}
