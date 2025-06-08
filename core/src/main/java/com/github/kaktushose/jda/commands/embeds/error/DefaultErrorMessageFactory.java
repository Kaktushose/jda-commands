package com.github.kaktushose.jda.commands.embeds.error;

import com.github.kaktushose.jda.commands.definitions.interactions.command.OptionDataDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.OptionDataDefinition.ConstraintDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import io.github.kaktushose.proteus.conversion.ConversionResult;
import io.github.kaktushose.proteus.type.Type;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/// Implementation of [ErrorMessageFactory] with default embeds.
///
/// @see JsonErrorMessageFactory
public class DefaultErrorMessageFactory implements ErrorMessageFactory {

    @NotNull
    @Override
    public MessageCreateData getTypeAdaptingFailedMessage(@NotNull ErrorContext context, @NotNull ConversionResult.Failure<?> failure) {
        SlashCommandDefinition command = (SlashCommandDefinition) context.definition();
        SlashCommandInteractionEvent event = (SlashCommandInteractionEvent) context.event();
        List<OptionDataDefinition> commandOptions = new ArrayList<>(command.commandOptions());
        List<OptionMapping> optionMappings = commandOptions
                .stream()
                .map(it -> event.getOption(it.name()))
                .toList();

        String name = "**%s**".formatted(command.displayName());
        String expected = "N/A";
        String actual = "N/A";
        String input = "N/A";
        for (int i = 0; i < commandOptions.size(); i++) {
            OptionDataDefinition commandOption = commandOptions.get(i);
            OptionMapping optionMapping = optionMappings.get(i);
            Type<?> into = Type.of(commandOption.type());
            if (failure.context() != null && into.equals(failure.context().into())) {
                name = "%s __%s__".formatted(name, commandOption.name());
                name = "%s %s".formatted(name, commandOptions.subList(i + 1, commandOptions.size())
                        .stream()
                        .map(OptionDataDefinition::name)
                        .collect(Collectors.joining(" ")));
                expected = commandOption.type().getSimpleName();
                actual = humanReadableType(optionMapping);
                input = optionMapping.getAsString();
                break;
            } else {
                name = "%s %s".formatted(name, commandOption.name());
            }
        }
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle("Invalid Arguments")
                .addField("Command", "%s".formatted(name.trim()), false)
                .addField("Expected Type", "`%s`".formatted(expected), true)
                .addField("Provided Type", "`%s`".formatted(actual), true)
                .addField("Raw Input", "`%s`".formatted(input), false)
                .addField("Details", failure.message(), false)
                .build();

        return new MessageCreateBuilder().setEmbeds(embed).build();
    }

    /// Gets the human-readable representation of an [OptionMapping].
    ///
    /// @param optionMapping the [OptionMapping] to return the human-readable representation for
    /// @return the human-readable representation
    @NotNull
    protected String humanReadableType(@NotNull OptionMapping optionMapping) {
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
            case UNKNOWN, SUB_COMMAND, SUB_COMMAND_GROUP -> throw new IllegalArgumentException(
                    "Invalid option type %s. Please report this error the the devs of jda-commands.".formatted(optionMapping)
            );
        };
    }

    @NotNull
    @Override
    public MessageCreateData getInsufficientPermissionsMessage(@NotNull ErrorContext context) {
        StringBuilder sbPermissions = new StringBuilder();
        context.definition().permissions().forEach(permission -> sbPermissions.append(permission).append(", "));
        String permissions = sbPermissions.toString().isEmpty() ? "N/A" : sbPermissions.substring(0, sbPermissions.length() - 2);
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Insufficient Permissions")
                .setDescription(String.format("`%s` requires specific permissions to be executed",
                        context.definition().displayName()))
                .addField("Permissions:",
                        String.format("`%s`", permissions), false
                ).build();
        return new MessageCreateBuilder().setEmbeds(embed).build();
    }

    @NotNull
    @Override
    public MessageCreateData getConstraintFailedMessage(@NotNull ErrorContext context, @NotNull ConstraintDefinition constraint) {
        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle("Parameter Error")
                .setDescription(String.format("```%s```", constraint.message()))
                .build()
        ).build();
    }

    @NotNull
    @Override
    public MessageCreateData getCooldownMessage(@NotNull ErrorContext context, long ms) {
        long secs = TimeUnit.MILLISECONDS.toSeconds(ms);
        long seconds = secs % 60;
        long minutes = (secs / 60) % 60;
        long hours = (secs / (60 * 60)) % 24;

        StringBuilder cooldown = new StringBuilder();
        if (hours > 0) {
            cooldown.append(hours).append(hours == 1 ? " hour" : " hours");
        }
        if (minutes > 0) {
            if (!cooldown.isEmpty()) {
                cooldown.append(" ");
            }
            cooldown.append(minutes).append(minutes == 1 ? " minute" : " minutes");
        }
        if (seconds > 0) {
            if (!cooldown.isEmpty()) {
                cooldown.append(" ");
            }
            cooldown.append(seconds).append(seconds == 1 ? " second" : " seconds");
        }
        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle("Cooldown")
                .setDescription(String.format("You cannot use this command for %s!", cooldown))
                .build()
        ).build();
    }

    @NotNull
    @Override
    public MessageCreateData getWrongChannelTypeMessage(@NotNull ErrorContext context) {
        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Wrong Channel Type")
                .setDescription("This command cannot be executed in this type of channels!")
                .build()
        ).build();
    }

    @NotNull
    @Override
    public MessageCreateData getCommandExecutionFailedMessage(@NotNull ErrorContext context, @NotNull Throwable exception) {
        String error;

        error = String.format("```The user \"%s\" attempted to execute an \"%s\" interaction at %s, " +
                        "but a \"%s\" occurred. " +
                        "Please refer to the logs for further information.```",
                context.event().getUser(),
                context.event().getInteraction().getType(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()),
                exception.getClass().getName()
        );

        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Command Execution Failed")
                .setDescription("The command execution has unexpectedly failed. Please report the following error to the bot devs.")
                .addField("Error Message", error, false)
                .build()
        ).build();
    }

    @NotNull
    @Override
    public MessageCreateData getTimedOutComponentMessage(@NotNull GenericInteractionCreateEvent context) {
        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Unknown Interaction")
                .setDescription("This interaction timed out and is no longer available!")
                .build()
        ).build();
    }
}
