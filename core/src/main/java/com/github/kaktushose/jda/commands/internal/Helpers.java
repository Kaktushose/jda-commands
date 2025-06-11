package com.github.kaktushose.jda.commands.internal;

import com.github.kaktushose.jda.commands.JDACBuilder;
import com.github.kaktushose.jda.commands.annotations.interactions.CommandConfig;
import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.description.ParameterDescription;
import com.github.kaktushose.jda.commands.definitions.features.internal.Invokable;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.MethodBuildContext;
import com.github.kaktushose.jda.commands.definitions.interactions.command.CommandDefinition;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory.ErrorContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.detached.IDetachableEntity;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

/// Collection of helper methods that are used inside the framework.
public final class Helpers {

    private static final Logger log = LoggerFactory.getLogger(Helpers.class);

    /// Sanitizes a String containing a raw mention. This will remove all markdown characters namely
    /// _`<`, `@`, `#`, `&`, `!`, `>`_.
    /// For instance: `<@!393843637437464588>` gets sanitized to `393843637437464588`.
    ///
    /// @param mention the raw String to sanitize
    /// @return the sanitized String

    public static String sanitizeMention(String mention) {
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

    public static Optional<GuildChannel> resolveGuildChannel(String raw, GenericInteractionCreateEvent event) {
        GuildChannel guildChannel;
        raw = sanitizeMention(raw);

        Guild guild = event.getGuild();
        if (guild == null || guild.isDetached()) {
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

    public static Set<String> permissions(MethodBuildContext context) {
        var permission = context.method().annotation(Permissions.class);

        if (permission.isPresent()) {
            HashSet<String> mergedPermissions = new HashSet<>(context.permissions());
            mergedPermissions.addAll(Set.of(permission.get().value()));
            return Collections.unmodifiableSet(mergedPermissions);
        }
        return context.permissions();
    }

    /// Checks if the given parameter is present at the [Method] at the given index.
    ///
    /// @param method the [Method] to check
    /// @param index  the index the parameter is expected to be at
    /// @param type   the type of the parameter
    /// @return `true` if the parameter is present
    public static boolean isIncorrectParameterType(MethodDescription method, int index, Class<?> type) {
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

    public static void checkDetached(IDetachableEntity entity, Class<?> origin) {
        if (entity.isDetached()) {
            throw new IllegalArgumentException("%s doesn't support detached entities and cannot be used for user installable apps!".formatted(origin.getName()));
        }
    }

    /// The [InteractionDefinition.ReplyConfig] that should be used when sending replies.
    ///
    /// @param definition the [`interaction definition`][Invokable] to build the [InteractionDefinition.ReplyConfig] from
    /// @param fallback   the [InteractionDefinition.ReplyConfig] to use as a fallback
    /// @implNote This will first attempt to use the [ReplyConfig] annotation of the method and then of the class. If
    /// neither is present will fall back to the global [InteractionDefinition.ReplyConfig] provided by [JDACBuilder].

    public static InteractionDefinition.ReplyConfig replyConfig(Invokable definition, InteractionDefinition.ReplyConfig fallback) {
        return computeConfig(
                ReplyConfig.class,
                definition.classDescription(),
                definition.methodDescription(),
                InteractionDefinition.ReplyConfig::new,
                fallback
        );
    }

    /// The [CommandDefinition.CommandConfig] that should be used when registering commands.
    ///
    /// @param context the [MethodBuildContext] to build the [CommandDefinition.CommandConfig] from
    /// @implNote This will first attempt to use the [CommandConfig] annotation of the method and then of the class. If
    /// neither is present will fall back to the global [CommandDefinition.CommandConfig] provided by [JDACBuilder].
    public static CommandDefinition.CommandConfig commandConfig(MethodBuildContext context) {
        return computeConfig(
                CommandConfig.class,
                context.clazz(),
                context.method(),
                CommandDefinition.CommandConfig::new,
                context.globalCommandConfig()
        );
    }

    /// Computes a config like [ReplyConfig] or [CommandConfig].
    ///
    /// This will first attempt to use the config annotation of  the method and then of the class. If neither is present
    /// will fall back to the provided fallback.
    ///
    /// @param annotation the [Annotation] defining the config
    /// @param clazz a [ClassDescription] where the annotation could be present
    /// @param method a [MethodDescription] where the annotation could be present
    /// @param mapper a [Function] to map the annotation to the representing data class
    /// @param fallback the fallback to use if no annotations are present
    /// @param <A> the annotation type of the config
    /// @param <C> the data class representing the config/ annotation
    /// @return C

    private static <A extends Annotation, C> C computeConfig(Class<A> annotation,
                                                             ClassDescription clazz,
                                                             MethodDescription method,
                                                             Function<A, C> mapper,
                                                             C fallback) {
        var clazzAnn = clazz.annotation(annotation);
        var methodAnn = method.annotation(annotation);

        if (clazzAnn.isEmpty() && methodAnn.isEmpty()) {
            return fallback;
        }

        return methodAnn.map(mapper).orElseGet(() -> mapper.apply(clazzAnn.get()));
    }


    public static ErrorContext errorContext(GenericInteractionCreateEvent event, InteractionDefinition definition) {
        return new ErrorContext() {


            @Override
            public GenericInteractionCreateEvent event() {
                return event;
            }


            @Override
            public InteractionDefinition definition() {
                return definition;
            }
        };
    }
}
