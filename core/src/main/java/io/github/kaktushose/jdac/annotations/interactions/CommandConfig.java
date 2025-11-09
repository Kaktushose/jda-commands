package io.github.kaktushose.jdac.annotations.interactions;

import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.definitions.interactions.command.CommandDefinition;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Used to configure the registration of commands.
///
/// Interaction methods annotated with [CommandConfig] will use the configured values of this annotation when registering.
/// Interaction classes annotated with [CommandConfig] will apply the configured values of this annotation to
/// every method, if and only if no annotation is present at method level. If the [CommandConfig] annotation is neither
/// present at the class level nor the method level, the global [`CommandConfig`][CommandDefinition.CommandConfig]
/// will be used instead.
///
///
/// In other words the hierarchy is as following:
/// 2. [ReplyConfig] method annotation
/// 3. [ReplyConfig] class annotation
/// 4. global [`CommandConfig`][CommandDefinition.CommandConfig] provided in [JDACBuilder]
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandConfig {

    /// The [InteractionContextType]s to use. The default value is [InteractionContextType#GUILD].
    ///
    /// @return the [InteractionContextType]s to use
    InteractionContextType[] context() default InteractionContextType.GUILD;

    /// The [IntegrationType]s to use. The default value is [IntegrationType#GUILD_INSTALL].
    ///
    /// @return the [IntegrationType]s to use
    IntegrationType[] integration() default IntegrationType.GUILD_INSTALL;

    /// The [CommandScope] to use. The default value is [CommandScope#GLOBAL].
    ///
    /// @return the [CommandScope] to use
    CommandScope scope() default CommandScope.GLOBAL;

    /// Whether the configured command(s) can only be executed in NSFW channels.
    ///
    /// @return `true` if the configured command(s) can only be executed in NSFW channels
    boolean isNSFW() default false;

    /// Returns an array of [Permission] this command
    /// should be enabled for by default. Note that guild admins can modify this at any time.
    ///
    /// @return an array of permissions this command will be enabled for by default
    /// @see Permissions Permission
    /// @see net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions DefaultMemberPermissions.ENABLED
    /// @see net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions DefaultMemberPermissions.DISABLED
    Permission[] enabledFor() default Permission.UNKNOWN;

}
