package com.github.kaktushose.jda.commands.embeds.error;

import com.github.kaktushose.jda.commands.JDACBuilder;
import com.github.kaktushose.jda.commands.definitions.interactions.command.OptionDataDefinition.ConstraintDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.embeds.Embed;
import com.github.kaktushose.jda.commands.embeds.EmbedDataSource;
import com.github.kaktushose.jda.commands.embeds.Embeds;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory.ErrorContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/// The default implementation of [ErrorMessageFactory]. Supports loading the embeds from an [EmbedDataSource].
///
/// @see JDACBuilder#embeds(Function)
public record DefaultErrorMessageFactory(Embeds embeds) implements ErrorMessageFactory {

    @NotNull
    @Override
    public MessageCreateData getTypeAdaptingFailedMessage(@NotNull ErrorContext context, @NotNull List<String> userInput) {
        StringBuilder sbExpected = new StringBuilder();
        SlashCommandDefinition command = (SlashCommandDefinition) context.definition();

        command.commandOptions().forEach(parameter -> {
            if (CommandEvent.class.isAssignableFrom(parameter.type())) {
                return;
            }
            String typeName = parameter.type().getTypeName();
            if (typeName.contains(".")) {
                typeName = typeName.substring(typeName.lastIndexOf(".") + 1);
            }
            sbExpected.append(typeName).append(", ");
        });
        String expected = sbExpected.toString().isEmpty() ? " " : sbExpected.substring(0, sbExpected.length() - 2);

        StringBuilder sbActual = new StringBuilder();
        userInput.forEach(argument -> sbActual.append(argument).append(", "));
        String actual = sbActual.toString().isEmpty() ? " " : sbActual.substring(0, sbActual.length() - 2);

        if (exists("typeAdaptingFailed")) {
            return embeds.get("typeAdaptingFailed")
                    .placeholder("usage", command.displayName())
                    .placeholder("expected", expected)
                    .placeholder("actual", actual)
                    .toMessageCreateData();
        }

        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle("Syntax Error")
                .setDescription(command.displayName())
                .addField("Expected", String.format("`%s`", expected), false)
                .addField("Actual", String.format("`%s`", actual), false)
                .build();

        return new MessageCreateBuilder().setEmbeds(embed).build();
    }

    @NotNull
    @Override
    public MessageCreateData getInsufficientPermissionsMessage(@NotNull ErrorContext context) {
        StringBuilder sbPermissions = new StringBuilder();
        context.definition().permissions().forEach(permission -> sbPermissions.append(permission).append(", "));
        String permissions = sbPermissions.toString().isEmpty() ? "N/A" : sbPermissions.substring(0, sbPermissions.length() - 2);

        if (exists("insufficientPermissions")) {
            return embeds.get("insufficientPermissions")
                    .placeholder("name", context.definition().displayName())
                    .placeholder("permissions", permissions)
                    .toMessageCreateData();
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

    @NotNull
    @Override
    public MessageCreateData getConstraintFailedMessage(@NotNull ErrorContext context, @NotNull ConstraintDefinition constraint) {
        if (exists("constraintFailed")) {
            return embeds.get("constraintFailed")
                    .placeholder("message", constraint.message())
                    .toMessageCreateData();
        }

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

        if (exists("cooldown")) {
            return embeds.get("cooldown")
                    .placeholder("cooldown", cooldown)
                    .toMessageCreateData();
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
    public MessageCreateData getCommandExecutionFailedMessage(@NotNull ErrorContext context, @NotNull Throwable exception) {
        String error = String.format("```The user \"%s\" attempted to execute an \"%s\" interaction at %s, " +
                        "but a \"%s\" occurred. " +
                        "Please refer to the logs for further information.```",
                context.event().getUser(),
                context.event().getInteraction().getType(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()),
                exception.getClass().getName()
        );

        if (exists("executionFailed")) {
            return embeds.get("executionFailed")
                    .placeholder("error", error)
                    .toMessageCreateData();
        }

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
    public MessageCreateData getTimedOutComponentMessage(@NotNull GenericInteractionCreateEvent event) {
        if (exists("unknownInteraction")) {
            return embeds.get("unknownInteraction").toMessageCreateData();
        }

        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Unknown Interaction")
                .setDescription("This interaction timed out and is no longer available!")
                .build()
        ).build();
    }

    private boolean exists(String name) {
        return embeds.sources().stream()
                .map(source -> source.get(name, embeds.placeholders()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny()
                .isPresent();
    }
}
