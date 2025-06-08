package com.github.kaktushose.jda.commands.annotations.interactions;

import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapters;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.Command.Type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Methods annotated with Command will be registered as a slash command at startup.
///
/// Therefore, the method must be declared inside a class that is annotated with [Interaction]. Both slash commands and
/// context commands are registered via this annotation.
/// # 1. Slash Commands
/// The method signature has to meet the following conditions:
///
///   - First parameter must be of type [CommandEvent]
///   - Remaining parameter types must be registered at the [TypeAdapters]
///
/// ## Examples:
/// ```
/// @Command("greet")
/// public void onCommand(CommandEvent event) {
///     event.reply("Hello World!");
/// }
///
/// @Command(value="moderation ban", desc="Bans a member", enabledFor=Permission.BAN_MEMBERS)
/// public void onCommand(CommandEvent event, @Param("The member to ban") Member target, @Param(optional = true, fallback = "no reason given") String reason) { ... }
///
/// @Command(value = "favourite fruit")
/// public void onCommand(CommandEvent event, @Choices({"Apple", "Banana", "Orange"}) String fruit) {
///     event.reply("You've chosen: %s", fruit);
/// }
/// ```
/// # Context Commands
/// The method signature has to meet the following conditions:
///
///   - First parameter must be of type [CommandEvent]
///   - Second parameter must either be a [User] or a [Message]
///
/// ## Examples:
/// ```
/// @Command(value = "message context command", type = Type.MESSAGE)
/// public void onCommand(CommandEvent event, Message target) { ... }
///
/// @Command(value = "user context command", type = Type.USER)
/// public void onCommand(CommandEvent event, User target) { ... }
/// ```
/// @see Interaction
/// @see com.github.kaktushose.jda.commands.annotations.interactions.Interaction Interaction
/// @see com.github.kaktushose.jda.commands.annotations.constraints.Constraint Constraint
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    /// Returns the name of the command.
    ///
    /// @return the name of the command
    String value() default "";

    /// Returns the description of the command.
    ///
    /// @return the description of the command
    String desc() default "N/A";

    /// Gets the type of this command.
    ///
    /// @return the type of the command
    Type type() default Type.SLASH;

}
