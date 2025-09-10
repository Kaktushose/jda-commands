package com.github.kaktushose.jda.commands.embeds.error;

import com.github.kaktushose.jda.commands.JDACBuilder;
import com.github.kaktushose.jda.commands.definitions.interactions.command.OptionDataDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.embeds.EmbedConfig;
import com.github.kaktushose.jda.commands.embeds.EmbedDataSource;
import com.github.kaktushose.jda.commands.embeds.internal.Embeds;
import com.github.kaktushose.jda.commands.i18n.I18n.Entry;
import com.github.kaktushose.jda.commands.internal.Helpers;
import io.github.kaktushose.proteus.conversion.ConversionResult;
import io.github.kaktushose.proteus.type.Type;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.github.kaktushose.jda.commands.i18n.I18n.entry;

/// The default implementation of [ErrorMessageFactory]. Supports loading the embeds from an [EmbedDataSource].
///
/// @see JDACBuilder#embeds(Consumer)
public record DefaultErrorMessageFactory(Embeds embeds) implements ErrorMessageFactory {

    /// {@inheritDoc}
    /// Use [EmbedConfig#errorSource(EmbedDataSource)] to replace the default embed of this error message. Alternatively,
    /// pass your own [ErrorMessageFactory] implementation to [JDACBuilder#errorMessageFactory(ErrorMessageFactory)].
    @Override
    public MessageCreateData getTypeAdaptingFailedMessage(ErrorContext context, ConversionResult.Failure<?> failure) {
        SlashCommandDefinition command = (SlashCommandDefinition) context.definition();
        SlashCommandInteractionEvent event = (SlashCommandInteractionEvent) context.event();
        List<OptionDataDefinition> commandOptions = new ArrayList<>(command.commandOptions());
        List<OptionMapping> optionMappings = commandOptions
                .stream()
                .map(it -> Objects.requireNonNull(event.getOption(it.name())))
                .toList();

        String name = "**%s**".formatted(command.displayName());
        String expected = "N/A";
        String actual = "N/A";
        String input = "N/A";
        for (int i = 0; i < commandOptions.size(); i++) {
            OptionDataDefinition commandOption = commandOptions.get(i);
            OptionMapping optionMapping = optionMappings.get(i);
            Type<?> into = Type.of(commandOption.declaredType());
            if (failure.context() != null && into.equals(failure.context().into())) {
                name = "%s __%s__".formatted(name, commandOption.name());
                name = "%s %s".formatted(name, commandOptions.subList(i + 1, commandOptions.size())
                        .stream()
                        .map(OptionDataDefinition::name)
                        .collect(Collectors.joining(" ")));
                expected = commandOption.declaredType().getSimpleName();
                actual = Helpers.humanReadableType(optionMapping);
                input = optionMapping.getAsString();
                break;
            } else {
                name = "%s %s".formatted(name, commandOption.name());
            }
        }

        if (embeds.exists("typeAdaptingFailed")) {
            return embeds.get("typeAdaptingFailed")
                    .placeholders(
                            entry("command", name.trim()),
                            entry("expected", "`%s`".formatted(expected)),
                            entry("actual", "`%s`".formatted(actual)),
                            entry("input", "`%s`".formatted(input)),
                            entry("details", failure.message())
                    ).toMessageCreateData();
        }

        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle("Invalid Arguments")
                .addField("Command", name.trim(), false)
                .addField("Expected Type", "`%s`".formatted(expected), true)
                .addField("Provided Type", "`%s`".formatted(actual), true)
                .addField("Raw Input", "`%s`".formatted(input), false)
                .addField("Details", failure.message(), false)
                .build();

        return new MessageCreateBuilder().setEmbeds(embed).build();
    }

    /// {@inheritDoc}
    /// Use [EmbedConfig#errorSource(EmbedDataSource)] to replace the default embed of this error message. Alternatively,
    /// pass your own [ErrorMessageFactory] implementation to [JDACBuilder#errorMessageFactory(ErrorMessageFactory)].
    @Override
    public MessageCreateData getInsufficientPermissionsMessage(ErrorContext context) {
        StringBuilder sbPermissions = new StringBuilder();
        context.definition().permissions().forEach(permission -> sbPermissions.append(permission).append(", "));
        String permissions = sbPermissions.toString().isEmpty() ? "N/A" : sbPermissions.substring(0, sbPermissions.length() - 2);

        if (embeds.exists("insufficientPermissions")) {
            return embeds.get("insufficientPermissions")
                    .placeholders(
                            new Entry("name", context.definition().displayName()),
                            new Entry("permissions", permissions)
                    ).toMessageCreateData();
        }

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

    /// {@inheritDoc}
    /// Use [EmbedConfig#errorSource(EmbedDataSource)] to replace the default embed of this error message. Alternatively,
    /// pass your own [ErrorMessageFactory] implementation to [JDACBuilder#errorMessageFactory(ErrorMessageFactory)].
    @Override
    public MessageCreateData getConstraintFailedMessage(ErrorContext context, String message) {
        if (embeds.exists("constraintFailed")) {
            return embeds.get("constraintFailed").placeholders(new Entry("message", message)).toMessageCreateData();
        }
        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle("Parameter Error")
                .setDescription(String.format("```%s```", message))
                .build()
        ).build();
    }

    /// {@inheritDoc}
    /// Use [EmbedConfig#errorSource(EmbedDataSource)] to replace the default embed of this error message. Alternatively,
    /// pass your own [ErrorMessageFactory] implementation to [JDACBuilder#errorMessageFactory(ErrorMessageFactory)].
    @Override
    public MessageCreateData getCooldownMessage(ErrorContext context, long ms) {
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

        if (embeds.exists("cooldown")) {
            return embeds.get("cooldown").placeholders(new Entry("cooldown", cooldown)).toMessageCreateData();
        }

        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle("Cooldown")
                .setDescription(String.format("You cannot use this command for %s!", cooldown))
                .build()
        ).build();
    }

    /// {@inheritDoc}
    /// Use [EmbedConfig#errorSource(EmbedDataSource)] to replace the default embed of this error message. Alternatively,
    /// pass your own [ErrorMessageFactory] implementation to [JDACBuilder#errorMessageFactory(ErrorMessageFactory)].
    @Override
    public MessageCreateData getCommandExecutionFailedMessage(ErrorContext context, Throwable exception) {
        String error = String.format("```The user \"%s\" attempted to execute an \"%s\" interaction at %s, " +
                                     "but a \"%s\" occurred. " +
                                     "Please refer to the logs for further information.```",
                context.event().getUser(),
                context.event().getInteraction().getType(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()),
                exception.getClass().getName()
        );

        if (embeds.exists("executionFailed")) {
            return embeds.get("executionFailed").placeholders(new Entry("error", error)).toMessageCreateData();
        }

        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Command Execution Failed")
                .setDescription("The command execution has unexpectedly failed. Please report the following error to the bot devs.")
                .addField("Error Message", error, false)
                .build()
        ).build();
    }

    /// {@inheritDoc}
    /// Use [EmbedConfig#errorSource(EmbedDataSource)] to replace the default embed of this error message. Alternatively,
    /// pass your own [ErrorMessageFactory] implementation to [JDACBuilder#errorMessageFactory(ErrorMessageFactory)].
    @Override
    public MessageCreateData getTimedOutComponentMessage(GenericInteractionCreateEvent event) {
        if (embeds.exists("unknownInteraction")) {
            return embeds.get("unknownInteraction").toMessageCreateData();
        }

        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Unknown Interaction")
                .setDescription("This interaction timed out and is no longer available!")
                .build()
        ).build();
    }
}
