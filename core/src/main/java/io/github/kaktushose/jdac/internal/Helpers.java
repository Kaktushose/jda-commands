package io.github.kaktushose.jdac.internal;

import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.annotations.interactions.CommandConfig;
import io.github.kaktushose.jdac.annotations.interactions.Permissions;
import io.github.kaktushose.jdac.annotations.interactions.ReplyConfig;
import io.github.kaktushose.jdac.definitions.description.ClassDescription;
import io.github.kaktushose.jdac.definitions.description.MethodDescription;
import io.github.kaktushose.jdac.definitions.description.ParameterDescription;
import io.github.kaktushose.jdac.definitions.features.internal.Invokable;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.MethodBuildContext;
import io.github.kaktushose.jdac.definitions.interactions.command.CommandDefinition;
import io.github.kaktushose.jdac.dispatching.events.interactions.CommandEvent;
import io.github.kaktushose.jdac.embeds.error.ErrorMessageFactory.ErrorContext;
import io.github.kaktushose.jdac.exceptions.InternalException;
import io.github.kaktushose.jdac.exceptions.InvalidDeclarationException;
import io.github.kaktushose.jdac.exceptions.internal.JDACException;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.detached.IDetachableEntity;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

/// Collection of helper methods that are used inside the framework.
@ApiStatus.Internal
public final class Helpers {

    /// Gets the human-readable representation of an [OptionMapping].
    ///
    /// @param optionMapping the [OptionMapping] to return the human-readable representation for
    /// @return the human-readable representation
    public static String humanReadableType(OptionMapping optionMapping) {
        return switch (optionMapping.getType()) {
            case STRING -> "String";
            case INTEGER -> "Long";
            case BOOLEAN -> "Boolean";
            case USER -> {
                Member member = optionMapping.getAsMember();
                if (member == null) {
                    yield "User";
                }
                yield "Member";
            }
            case CHANNEL -> "Channel";
            case ROLE -> "Role";
            case MENTIONABLE -> "Mentionable (Role, User, Member)";
            case NUMBER -> "Double";
            case ATTACHMENT -> "Attachment";
            case UNKNOWN, SUB_COMMAND, SUB_COMMAND_GROUP -> throw new InternalException(
                    "invalid-option-type", entry("type", optionMapping)
            );
        };
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
    public static void checkParameterType(MethodDescription method, int index, Class<?> type) {
        if (!type.isAssignableFrom(List.copyOf(method.parameters()).get(index).type())) {
            throw new InvalidDeclarationException("invalid-parameter", entry("index", index + 1), entry("name", type.getSimpleName()));
        }
    }

    public static void checkSignature(MethodDescription method, SequencedCollection<Class<?>> methodSignature) {
        var parameters = method.parameters().stream()
                .map(ParameterDescription::type)
                .toList();
        if (!parameters.equals(methodSignature)) {

            String prefix = !parameters.isEmpty() && parameters.getFirst().equals(methodSignature.getFirst())
                    ? ""
                    : JDACException.errorMessage("incorrect-method-signature", entry("parameter", methodSignature.getFirst().getName()));

            throw new InvalidDeclarationException("incorrect-method-signature",
                    entry("prefix", prefix),
                    entry("expected", methodSignature.stream().toList().toString()),
                    entry("actual", method.parameters().stream().map(ParameterDescription::type).toList().toString())
            );
        }
    }

    public static void checkSignatureUserContext(MethodDescription method) {
        var parameters = method.parameters().stream()
                .map(ParameterDescription::type)
                .toList();
        if (!(parameters.equals(List.of(CommandEvent.class, User.class)) || parameters.equals(List.of(CommandEvent.class, Member.class)))) {

            String prefix = !parameters.isEmpty() && parameters.getFirst().equals(CommandEvent.class)
                    ? ""
                    : JDACException.errorMessage("incorrect-method-signature", entry("parameter", CommandEvent.class.getName()));

            throw new InvalidDeclarationException("incorrect-method-signature",
                    entry("prefix", prefix),
                    entry("expected", "[%s, %s OR %s]".formatted(CommandEvent.class, User.class, Member.class)),
                    entry("actual", method.parameters().stream().map(ParameterDescription::type).toList().toString())
            );
        }
    }

    public static void checkDetached(IDetachableEntity entity, Class<?> origin) {
        if (entity.isDetached()) {
            throw new IllegalArgumentException(JDACException.errorMessage("detached-entity", entry("class", origin.getName())));
        }
    }

    public static InvalidDeclarationException jdaException(IllegalArgumentException cause, InteractionDefinition definition) {
        return new InvalidDeclarationException(
                "jda-exception",
                entry("cause", cause.getMessage()),
                entry("type", definition.displayName()),
                entry("class", definition.classDescription().name()),
                entry("method", definition.methodDescription().name())
        );
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
    /// @param clazz      a [ClassDescription] where the annotation could be present
    /// @param method     a [MethodDescription] where the annotation could be present
    /// @param mapper     a [Function] to map the annotation to the representing data class
    /// @param fallback   the fallback to use if no annotations are present
    /// @param <A>        the annotation type of the config
    /// @param <C>        the data class representing the config/ annotation
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
