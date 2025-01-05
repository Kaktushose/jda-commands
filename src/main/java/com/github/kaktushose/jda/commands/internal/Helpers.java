package com.github.kaktushose.jda.commands.internal;

import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.description.ParameterDescription;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.MethodBuildContext;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory.ErrorContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

/// Collection of helper methods that are used inside the framework.
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
        var permission = context.method().annotation(Permissions.class);

        if (permission.isPresent()) {
            HashSet<String> mergedPermissions = new HashSet<>(context.permissions());
            mergedPermissions.addAll(Set.of(permission.get().value()));
            return Collections.unmodifiableSet(mergedPermissions);
        }
        return context.permissions();
    }

    /// Constructs the [InteractionDefinition.ReplyConfig ReplyConfig] based on the passed [Method].
    ///
    /// @param method the [Method] to use
    /// @param fallbackReplyConfig the [InteractionDefinition.ReplyConfig] to be used as a fallback
    /// @return the [InteractionDefinition.ReplyConfig ReplyConfig]
    /// @implNote This will first attempt to use the [ReplyConfig] annotation of the method and then of the class.
    /// If neither is present will fall back to the provided fallback.
    @NotNull
    public static InteractionDefinition.ReplyConfig replyConfig(@NotNull Method method, InteractionDefinition.ReplyConfig fallbackReplyConfig) {
        var global = method.getDeclaringClass().getAnnotation(ReplyConfig.class);
        var local = method.getAnnotation(ReplyConfig.class);

        if (global == null && local == null)
            return new InteractionDefinition.ReplyConfig();
        if (local == null)
            return new InteractionDefinition.ReplyConfig(global);

        return new InteractionDefinition.ReplyConfig(local);
    }

    /// Checks if the given parameter is present at the [Method] at the given index.
    ///
    /// @param method the [Method] to check
    /// @param index  the index the parameter is expected to be at
    /// @param type   the type of the parameter
    /// @return `true` if the parameter is present
    public static boolean isIncorrectParameterType(@NotNull MethodDescription method, int index, @NotNull Class<?> type) {
        if (!type.isAssignableFrom(List.copyOf(method.parameters()).get(index).type())) {
            log.error("An error has occurred! Skipping Interaction {}.{}:",
                    method.declaringClass().getName(),
                    method.name(),
                    new IllegalArgumentException(String.format("%d. parameter must be of type %s", index + 1, type.getSimpleName())));
            return true;
        }
        return false;
    }

    public static boolean checkSignature(MethodDescription method, Collection<Class<?>> methodSignature) {
        var parameters = method.parameters().stream()
                .map(ParameterDescription::type)
                .toList();
        if (!parameters.equals(methodSignature)) {
            log.error("An error has occurred! Skipping Interaction {}.{}:",
                    method.declaringClass().getName(),
                    method.name(),
                    new IllegalArgumentException("Incorrect method signature!\nExpected: %s\nActual:   %s".formatted(
                            methodSignature.stream().toList(),
                            method.parameters().stream().map(ParameterDescription::type).toList()
                    )));
            return true;
        }
        return false;
    }

    @NotNull
    public static ErrorContext errorContext(@NotNull GenericInteractionCreateEvent event, @NotNull InteractionDefinition definition) {
        return new ErrorContext() {

            @NotNull
            @Override
            public GenericInteractionCreateEvent event() {
                return event;
            }

            @NotNull
            @Override
            public InteractionDefinition definition() {
                return definition;
            }
        };
    }
}
