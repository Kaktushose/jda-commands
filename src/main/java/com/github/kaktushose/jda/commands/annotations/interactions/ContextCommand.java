package com.github.kaktushose.jda.commands.annotations.interactions;

import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Methods annotated with ContextMenu will be registered as a context menu command.
///
/// Therefore, the method must be declared inside a class that is annotated with
/// [Interaction].
/// Furthermore, the method signature has to meet the following conditions:
///
///   - First parameter must be of type [CommandEvent]
///   - Second parameter must either be a [User] or a [Message]
///
/// ## Examples:
/// ```
/// @ContextCommand(value = "message context command", type = Command.Type.MESSAGE)
/// public void onCommand(CommandEvent event, Message target) { ... }
///
/// @ContextCommand(value = "user context command", type = Command.Type.USER)
/// public void onCommand(CommandEvent event, User target) { ... }
/// ```
/// @see Interaction
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ContextCommand {

    /// Returns the name of the command.
    ///
    /// @return the name of the command
    String value() default "";

    /// Returns whether this command is only usable in a guild.
    /// This only has an effect if this command is registered globally.
    ///
    /// @return `true` if this command is only usable in a guild
    boolean isGuildOnly() default false;

    /// Returns whether this command can only be executed in NSFW channels.
    ///
    /// @return `true` if this command can only be executed in NSFW channels
    boolean isNSFW() default false;

    /// Returns an array of [Permission] this command
    /// should be enabled for by default. Note that guild admins can modify this at any time.
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

    /// Gets the type of this command.
    ///
    /// @return the type of the command
    Command.Type type();

}
