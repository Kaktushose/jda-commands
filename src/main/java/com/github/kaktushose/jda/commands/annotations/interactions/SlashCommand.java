package com.github.kaktushose.jda.commands.annotations.interactions;

import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import net.dv8tion.jda.api.Permission;

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
///   - Remaining parameter types must be registered at the [TypeAdapterRegistry]
///   - or the second parameter is a String array
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

    /// Returns whether this command is only usable in a guild.
    /// This only has an effect if this command is registered globally.
    ///
    /// @return `true` if this command is only usable in a guild
    boolean isGuildOnly() default false;

    /// Returns the description of the command.
    ///
    /// @return the description of the command
    String desc() default "N/A";

    /// Returns whether this command can only be executed in NSFW channels.
    ///
    /// @return `true` if this command can only be executed in NSFW channels
    boolean isNSFW() default false;

    /// Returns an array of [Permission]s this command should be enabled for by default. Note that guild admins can
    /// modify this at any time.
    ///
    /// @return an array of permissions this command will be enabled for by default
    /// @see Permissions Permission
    /// @see net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions DefaultMemberPermissions.ENABLED
    /// @see net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions DefaultMemberPermissions.DISABLED
    Permission[] enabledFor() default Permission.UNKNOWN;

    /// Returns whether this command should be registered as a global or as a guild command.
    ///
    /// @return whether this command should be registered as a global or as a guild command
    /// @see CommandScope
    CommandScope scope() default CommandScope.GLOBAL;

}
