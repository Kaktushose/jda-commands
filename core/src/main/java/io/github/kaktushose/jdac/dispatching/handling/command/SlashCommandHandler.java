package io.github.kaktushose.jdac.dispatching.handling.command;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.command.OptionDataDefinition;
import io.github.kaktushose.jdac.definitions.interactions.command.SlashCommandDefinition;
import io.github.kaktushose.jdac.dispatching.Runtime;
import io.github.kaktushose.jdac.dispatching.events.interactions.CommandEvent;
import io.github.kaktushose.jdac.dispatching.handling.EventHandler;
import io.github.kaktushose.jdac.exceptions.InternalException;
import io.github.kaktushose.jdac.internal.Helpers;
import io.github.kaktushose.jdac.introspection.internal.IntrospectionImpl;
import io.github.kaktushose.proteus.Proteus;
import io.github.kaktushose.proteus.conversion.ConversionResult;
import io.github.kaktushose.proteus.type.Type;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.*;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

@ApiStatus.Internal
public final class SlashCommandHandler extends EventHandler<SlashCommandInteractionEvent> {

    public static final ScopedValue<Locale> USER_LOCALE = ScopedValue.newInstance();

    private static final Map<Class<?>, Object> DEFAULT_MAPPINGS = Map.of(
            byte.class, ((byte) 0),
            short.class, ((short) 0),
            int.class, 0,
            long.class, 0L,
            double.class, 0.0d,
            float.class, 0.0f,
            boolean.class, false,
            char.class, '\u0000',
            Optional.class, Optional.empty()
    );

    public SlashCommandHandler(IntrospectionImpl introspection) {
        super(introspection);
    }

    @Override
    @Nullable
    protected PreparationResult prepare(SlashCommandInteractionEvent event, Runtime runtime) {
        SlashCommandDefinition command = interactionRegistry.find(SlashCommandDefinition.class, true, it ->
                it.name().equals(event.getFullCommandName())
        );

        // Scope values needed for user locale in proteus mapper, see type adapter
        return ScopedValue.where(USER_LOCALE, event.getUserLocale().toLocale())
                .call(() -> parseArguments(command, event).map(args -> new PreparationResult(command, args)))
                .orElse(null);

    }

    @SuppressWarnings("unchecked")
    private Optional<List<@Nullable Object>> parseArguments(SlashCommandDefinition command, SlashCommandInteractionEvent event) {
        List<@Nullable OptionMapping> optionMappings = command
                .commandOptions()
                .stream()
                .map(it -> event.getOption(it.name()))
                .toList();
        InteractionDefinition.ReplyConfig replyConfig = Helpers.replyConfig(command, runtimeIntrospection.get(Property.GLOBAL_REPLY_CONFIG));
        List<@Nullable Object> parsedArguments = new ArrayList<>();

        log.debug("Type adapting arguments...");
        var optionDataDefinitions = List.copyOf(command.commandOptions());
        parsedArguments.addFirst(new CommandEvent());

        if (optionMappings.size() != optionDataDefinitions.size()) {
            throw new InternalException("command-input-mismatch");
        }

        Proteus proteus = Proteus.global();
        for (int i = 0; i < optionDataDefinitions.size(); i++) {
            OptionDataDefinition optionData = optionDataDefinitions.get(i);
            OptionMapping optionMapping = optionMappings.get(i);
            if (optionMapping == null) {
                parsedArguments.add(DEFAULT_MAPPINGS.getOrDefault(optionData.declaredType(), null));
                continue;
            }
            Type<?> sourceType = toType(optionMapping);
            Type<?> targetType = Type.of(optionData.resolvedType());

            log.debug("Trying to adapt input '{}' as type '{}' to type '{}'", optionMapping, sourceType, targetType);
            ConversionResult<?> result = proteus.convert(toValue(optionMapping), (Type<Object>) sourceType, (Type<Object>) targetType);

            switch (result) {
                case ConversionResult.Success<?>(Object success, boolean _) -> {
                    if (optionData.declaredType().equals(Optional.class)) {
                        parsedArguments.add(Optional.of(success));
                    } else {
                        parsedArguments.add(success);
                    }
                }
                case ConversionResult.Failure<?> failure -> {
                    switch (failure.errorType()) {
                        case MAPPING_FAILED -> {
                            log.debug("Type adapting failed!");
                            event.replyComponents(errorMessageFactory.getTypeAdaptingFailedMessage(Helpers.errorContext(event, command), failure))
                                    .useComponentsV2()
                                    .setEphemeral(replyConfig.ephemeral())
                                    .setSuppressedNotifications(replyConfig.silent())
                                    .queue();
                            return Optional.empty();
                        }
                        case NO_PATH_FOUND, NO_LOSSLESS_CONVERSION -> throw new InternalException(
                                "proteus-error", entry("message", failure.detailedMessage())
                        );
                    }
                }
            }
        }
        return Optional.of(parsedArguments);
    }

    private Type<?> toType(OptionMapping optionMapping) {
        OptionType type = optionMapping.getType();
        return switch (type) {
            case STRING -> Type.of(String.class);
            case INTEGER -> Type.of(Long.class);
            case BOOLEAN -> Type.of(Boolean.class);
            case USER -> {
                Member member = optionMapping.getAsMember();
                if (member == null) {
                    yield Type.of(User.class);
                }
                yield Type.of(Member.class);
            }
            case CHANNEL -> Type.of(GuildChannelUnion.class);
            case ROLE -> Type.of(Role.class);
            case MENTIONABLE -> Type.of(IMentionable.class);
            case NUMBER -> Type.of(Double.class);
            case ATTACHMENT -> Type.of(Message.Attachment.class);
            case UNKNOWN, SUB_COMMAND, SUB_COMMAND_GROUP -> throw new InternalException(
                    "invalid-option-type", entry("type", type.name())
            );
        };
    }

    private Object toValue(OptionMapping optionMapping) {
        return switch (optionMapping.getType()) {
            case STRING -> optionMapping.getAsString();
            case INTEGER -> optionMapping.getAsLong();
            case BOOLEAN -> optionMapping.getAsBoolean();
            case USER -> {
                Member member = optionMapping.getAsMember();
                if (member == null) {
                    yield optionMapping.getAsUser();
                }
                yield member;
            }
            case CHANNEL -> optionMapping.getAsChannel();
            case ROLE -> optionMapping.getAsRole();
            case MENTIONABLE -> optionMapping.getAsMentionable();
            case NUMBER -> optionMapping.getAsDouble();
            case ATTACHMENT -> optionMapping.getAsAttachment();
            case UNKNOWN, SUB_COMMAND, SUB_COMMAND_GROUP -> throw new InternalException(
                    "invalid-option-type", entry("type", optionMapping.getName())
            );
        };
    }

}
