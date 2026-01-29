package io.github.kaktushose.jdac.annotations.interactions;

import io.github.kaktushose.jdac.dispatching.adapter.internal.TypeAdapters;
import io.github.kaktushose.jdac.dispatching.events.interactions.CommandEvent;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.Command.Type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;

/// Methods annotated with Command will be registered as a slash command at startup.
///
/// Therefore, the method must be declared inside a class that is annotated with [Interaction]. Both slash commands and
/// context commands are registered via this annotation.
/// # 1. Slash Commands
/// The method signature has to meet the following conditions:
///
///   - First parameter must be of type [CommandEvent]
///   - Remaining parameter types must be registered at the [TypeAdapters]. Please note that [Optional] are handled
/// special, see next headline.
///
/// ### [Optional] as parameter type
/// Beside defining parameters with [Param#optional()] set to true, it's also possible to wrap it in an [Optional]
/// with the appropriated type set as the generic type parameter. (Example: `@Param(optional = true) String one` -> `
/// Optional<String> one`.
///
/// JDA-Commands will register this parameter as optional to discord. If the option isn't provided by the user,
///  [Optional#empty()] is passed.
///
///
///
/// ## Examples:
/// ```
/// @Command("greet")
/// public void onCommand(CommandEvent event) {
///     event.reply("Hello World!");
/// }
///
/// @Command(value="moderation ban", desc="Bans a member", enabledFor=Permission.BAN_MEMBERS)
/// public void onCommand(CommandEvent event, @Param("The member to ban") Member target, @Param(optional = true,
///  fallback = "no reason given") String reason) { ... }
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
///   - Second parameter must either be a [User], [Member] or a [Message]
///
/// ## Examples:
/// ```
/// @Command(value = "message context command", type = Type.MESSAGE)
/// public void onCommand(CommandEvent event, Message target) { ... }
///
/// @Command(value = "user context command", type = Type.USER)
/// public void onCommand(CommandEvent event, User target) { ... }
///
/// @Command(value = "member context command", type = Type.USER)
/// public void onCommand(CommandEvent event, Member target) { ... }
/// ```
/// **Using [Member] will enforce [InteractionContextType#GUILD] on the command!**
///
/// @see Interaction
/// @see io.github.kaktushose.jdac.annotations.interactions.Interaction Interaction
/// @see io.github.kaktushose.jdac.annotations.constraints.Constraint Constraint
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
