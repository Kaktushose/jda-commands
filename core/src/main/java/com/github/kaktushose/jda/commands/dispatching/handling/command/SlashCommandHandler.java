package com.github.kaktushose.jda.commands.dispatching.handling.command;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.OptionDataDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.DispatchingContext;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.handling.EventHandler;
import com.github.kaktushose.jda.commands.dispatching.reply.internal.ReplyAction;
import com.github.kaktushose.jda.commands.internal.Helpers;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApiStatus.Internal
public final class SlashCommandHandler extends EventHandler<SlashCommandInteractionEvent> {

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

    public SlashCommandHandler(DispatchingContext dispatchingContext) {
        super(dispatchingContext);
    }

    @Override
    @Nullable
    protected InvocationContext<SlashCommandInteractionEvent> prepare(SlashCommandInteractionEvent event, Runtime runtime) {
        SlashCommandDefinition command = registry.find(SlashCommandDefinition.class, true, it ->
                it.name().equals(event.getFullCommandName())
        );

        return parseArguments(command, event, runtime)
                .map(args -> new InvocationContext<>(
                        event,
                        dispatchingContext.i18n(),
                        runtime.keyValueStore(),
                        command,
                        Helpers.replyConfig(command, dispatchingContext.globalReplyConfig()),
                        args)
                ).orElse(null);
    }

    @SuppressWarnings("unchecked")
    private Optional<List<@Nullable Object>> parseArguments(SlashCommandDefinition command, SlashCommandInteractionEvent event, Runtime runtime) {
        List<@Nullable OptionMapping> optionMappings = command
                .commandOptions()
                .stream()
                .map(it -> event.getOption(it.name()))
                .toList();
        InteractionDefinition.ReplyConfig replyConfig = Helpers.replyConfig(command, dispatchingContext.globalReplyConfig());
        List<@Nullable Object> parsedArguments = new ArrayList<>();

        log.debug("Type adapting arguments...");
        var optionDataDefinitions = List.copyOf(command.commandOptions());
        parsedArguments.addFirst(new CommandEvent(event, registry, runtime, command, replyConfig, dispatchingContext.embeds()));

        if (optionMappings.size() != optionDataDefinitions.size()) {
            throw new IllegalStateException(
                    "Command input doesn't match command options length! Please report this error to the devs of jda-commands."
            );
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
                            new ReplyAction(event, command, dispatchingContext.i18n(), replyConfig).reply(
                                    errorMessageFactory.getTypeAdaptingFailedMessage(Helpers.errorContext(event, command), failure)
                            );
                            return Optional.empty();
                        }
                        case NO_PATH_FOUND, NO_LOSSLESS_CONVERSION -> throw new IllegalStateException(
                                "Proteus Error: %s. Please report this error to the devs of jda-commands.".formatted(failure.detailedMessage())
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
            case UNKNOWN, SUB_COMMAND, SUB_COMMAND_GROUP -> throw new IllegalArgumentException(
                    "Invalid option type %s. Please report this error to the devs of jda-commands.".formatted(type)
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
            case UNKNOWN, SUB_COMMAND, SUB_COMMAND_GROUP -> throw new IllegalArgumentException(
                    "Invalid option type %s. Please report this error to the devs of jda-commands.".formatted(optionMapping)
            );
        };
    }

}
