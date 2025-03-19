package com.github.kaktushose.jda.commands.annotations.interactions;

import com.github.kaktushose.jda.commands.JDACBuilder;
import com.github.kaktushose.jda.commands.definitions.interactions.command.CommandDefinition;
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

}
