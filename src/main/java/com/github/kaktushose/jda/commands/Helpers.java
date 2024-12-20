package com.github.kaktushose.jda.commands;

import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig;
import com.github.kaktushose.jda.commands.reflect.MethodBuildContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/// Collection of helper methods that are used inside the framework.
///
/// @since 4.0.0
@ApiStatus.Internal
public final class Helpers {

    private static final Logger log = LoggerFactory.getLogger(Helpers.class);

    /// Sanitizes a String containing a raw mention. This will remove all markdown characters namely
    /// _`<`, `@`, `#`, `&`, `!`, `>`_.
    /// For instance: `<@!393843637437464588>` gets sanitized to `393843637437464588`.
    ///
    /// @param mention the raw String to sanitize
    /// @return the sanitized String
    @NotNull
    public static String sanitizeMention(@NotNull String mention) {
        if (mention.matches("<[@#][&!]?([0-9]{4,})>")) {
            return mention.replaceAll("<[@#][&!]?", "").replace(">", "");
        }
        return mention;
    }

    /// Attempts to resolve a [GuildChannel] based on user input.
    ///
    /// @param raw   the String the [GuildChannel] should be resolved from
    /// @param event the corresponding [GenericInteractionCreateEvent]
    /// @return an [Optional] holding the resolved [GuildChannel] or an empty [Optional] if the resolving failed
    @NotNull
    public static Optional<GuildChannel> resolveGuildChannel(@NotNull String raw, @NotNull GenericInteractionCreateEvent event) {
        GuildChannel guildChannel;
        raw = sanitizeMention(raw);

        Guild guild = event.getGuild();
        if (guild == null) {
            return Optional.empty();
        }
        if (raw.matches("\\d+")) {
            guildChannel = guild.getGuildChannelById(raw);
        } else {
            String finalRaw = raw;
            guildChannel = guild.getChannels().stream().filter(it -> it.getName().equalsIgnoreCase(finalRaw))
                    .findFirst().orElse(null);
        }
        return Optional.ofNullable(guildChannel);
    }

    /// Extracts the permissions from a [MethodBuildContext]. This combines the permissions of the method and the class.
    ///
    /// @param context the [MethodBuildContext] to extract the permissions from
    /// @return a possibly-empty set of all permissions
    @NotNull
    public static Set<String> permissions(@NotNull MethodBuildContext context) {
        Permissions permission = context.method().getAnnotation(Permissions.class);

        if (permission != null) {
            HashSet<String> mergedPermissions = new HashSet<>(context.permissions());
            mergedPermissions.addAll(Set.of(permission.value()));
            return Collections.unmodifiableSet(mergedPermissions);
        }
        return context.permissions();
    }

    /// Constructs the [com.github.kaktushose.jda.commands.reflect.interactions.ReplyConfig ReplyConfig] based on the
    /// passed [Method].
    ///
    /// This will first attempt to use the [ReplyConfig] annotation of the method and then of the class. If neither is
    /// present will fall back to the
    /// [com.github.kaktushose.jda.commands.dispatching.reply.GlobalReplyConfig GlobalReplyConfig].
    ///
    /// @param method the [Method] to use
    /// @return the [com.github.kaktushose.jda.commands.reflect.interactions.ReplyConfig ReplyConfig]
    @NotNull
    public static com.github.kaktushose.jda.commands.reflect.interactions.ReplyConfig replyConfig(@NotNull Method method) {
        var global = method.getDeclaringClass().getAnnotation(ReplyConfig.class);
        var local = method.getAnnotation(ReplyConfig.class);

        if (global == null && local == null)
            return new com.github.kaktushose.jda.commands.reflect.interactions.ReplyConfig();
        if (local == null)
            return new com.github.kaktushose.jda.commands.reflect.interactions.ReplyConfig(global);

        return new com.github.kaktushose.jda.commands.reflect.interactions.ReplyConfig(local);
    }

    /// Checks if the given parameter is present at the [Method] at the given index.
    ///
    /// @param method the [Method] to check
    /// @param index  the index the parameter is expected to be at
    /// @param type   the type of the parameter
    /// @return `true` if the parameter is present
    public static boolean isIncorrectParameterType(@NotNull Method method, int index, @NotNull Class<?> type) {
        if (!type.isAssignableFrom(method.getParameters()[index].getType())) {
            log.error("An error has occurred! Skipping Interaction {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException(String.format("%d. parameter must be of type %s", index + 1, type.getSimpleName())));
            return true;
        }
        return false;
    }

    /// Checks if a [Method] has the given parameter count.
    ///
    /// @param count the parameter count
    /// @return `true` if the method has the given parameter count
    public static boolean isIncorrectParameterAmount(@NotNull Method method, int count) {
        if (method.getParameters().length != count) {
            log.error("An error has occurred! Skipping Interaction {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException(String.format(
                            "Invalid amount of parameters!. Expected: %d Actual: %d",
                            count,
                            method.getParameters().length
                    )));
            return true;
        }
        return false;
    }
}
