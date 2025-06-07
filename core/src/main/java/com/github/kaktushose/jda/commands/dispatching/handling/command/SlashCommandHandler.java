package com.github.kaktushose.jda.commands.dispatching.handling.command;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.OptionDataDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.DispatchingContext;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapters;
import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapters.TypeFormat;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.handling.EventHandler;
import com.github.kaktushose.jda.commands.dispatching.reply.internal.MessageCreateDataReply;
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
import org.jetbrains.annotations.NotNull;

import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApiStatus.Internal
public final class SlashCommandHandler extends EventHandler<SlashCommandInteractionEvent> {

    public SlashCommandHandler(DispatchingContext dispatchingContext) {
        super(dispatchingContext);
    }

    @Override
    protected InvocationContext<SlashCommandInteractionEvent> prepare(@NotNull SlashCommandInteractionEvent event, @NotNull Runtime runtime) {
        SlashCommandDefinition command = registry.find(SlashCommandDefinition.class, true, it ->
                it.name().equals(event.getFullCommandName())
        );

        return parseArguments(command, event, runtime)
                .map(args -> new InvocationContext<>(
                        event,
                        runtime.keyValueStore(),
                        command,
                        Helpers.replyConfig(command, dispatchingContext.globalReplyConfig()),
                        args)
                ).orElse(null);
    }

    @SuppressWarnings("unchecked")
    private Optional<List<Object>> parseArguments(SlashCommandDefinition command, SlashCommandInteractionEvent event, Runtime runtime) {
        List<OptionMapping> optionMappings = command
                .commandOptions()
                .stream()
                .map(it -> event.getOption(it.name()))
                .toList();
        InteractionDefinition.ReplyConfig replyConfig = Helpers.replyConfig(command, dispatchingContext.globalReplyConfig());
        List<Object> parsedArguments = new ArrayList<>();

        log.debug("Type adapting arguments...");
        var optionDataDefinitions = List.copyOf(command.commandOptions());
        parsedArguments.addFirst(new CommandEvent(event, registry, runtime, command, replyConfig));

        if (optionMappings.size() != optionDataDefinitions.size()) {
            throw new IllegalStateException(
                    "Command input doesn't match command options length! Please report this error the the devs of jda-commands."
            );
        }

        for (int i = 0; i < optionDataDefinitions.size(); i++) {
            Proteus proteus = Proteus.global();
            OptionDataDefinition optionData = optionDataDefinitions.get(i);
            OptionMapping optionMapping = optionMappings.get(i);
            Type<?> sourceType = toType(optionMapping);
            Type<?> targetType = Type.of(new TypeFormat(optionData.optionType()), optionData.type());

            log.debug("Trying to adapt input '{}' as type '{}' to type '{}'", optionMapping, sourceType, targetType);
            System.out.println(proteus.existsPath(sourceType, targetType));
            ConversionResult<?> result = proteus.convert(toValue(optionMapping), (Type<Object>) sourceType, (Type<Object>) targetType);

            switch (result) {
                case ConversionResult.Success<?>(Object success, boolean _) -> parsedArguments.add(success);
                case ConversionResult.Failure<?> failure -> {
                    switch (failure.errorType()) {
                        case MAPPING_FAILED -> {
                            log.debug("Type adapting failed!");
                            MessageCreateDataReply.reply(event, command, replyConfig,
                                    errorMessageFactory.getTypeAdaptingFailedMessage(Helpers.errorContext(event, command), optionMappings
                                            .stream()
                                            .map(it -> it == null ? null : it.getAsString())
                                            .toList())
                            );
                            return Optional.empty();
                        }
                        case NO_PATH_FOUND, NO_LOSSLESS_CONVERSION -> throw new IllegalStateException(
                                "Proteus Error: %s. Please report this error the the devs of jda-commands.".formatted(failure.detailedMessage())
                        );
                    }
                }
            }
        }
        return Optional.of(parsedArguments);
    }

    private Type<?> toType(OptionMapping optionMapping) {
        OptionType type = optionMapping.getType();
        TypeFormat format = new TypeFormat(type);
        return switch (type) {
            case STRING -> Type.of(format, String.class);
            case INTEGER -> Type.of(format, Long.class);
            case BOOLEAN -> Type.of(format, Boolean.class);
            case USER -> {
                Member member = optionMapping.getAsMember();
                if (member == null) {
                    yield Type.of(format, User.class);
                }
                yield Type.of(format, Member.class);
            }
            case CHANNEL -> Type.of(format, GuildChannelUnion.class);
            case ROLE -> Type.of(format, Role.class);
            case MENTIONABLE -> Type.of(format, IMentionable.class);
            case NUMBER -> Type.of(format, Double.class);
            case ATTACHMENT -> Type.of(format, Message.Attachment.class);
            case UNKNOWN, SUB_COMMAND, SUB_COMMAND_GROUP -> throw new IllegalArgumentException(
                    "Invalid option type %s. Please report this error the the devs of jda-commands.".formatted(type)
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
                    "Invalid option type %s. Please report this error the the devs of jda-commands.".formatted(optionMapping)
            );
        };
    }

}
