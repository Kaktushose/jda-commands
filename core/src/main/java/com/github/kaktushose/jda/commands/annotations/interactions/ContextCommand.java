package com.github.kaktushose.jda.commands.annotations.interactions;

import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
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

    /// Gets the type of this command.
    ///
    /// @return the type of the command
    Command.Type type();

}
