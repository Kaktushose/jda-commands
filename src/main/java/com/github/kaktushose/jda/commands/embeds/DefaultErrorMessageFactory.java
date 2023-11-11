package com.github.kaktushose.jda.commands.embeds;

import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.SlashCommandContext;
import com.github.kaktushose.jda.commands.reflect.ConstraintDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.SlashCommandDefinition;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of {@link ErrorMessageFactory} with default embeds.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @see JsonErrorMessageFactory
 * @since 2.0.0
 */
public class DefaultErrorMessageFactory implements ErrorMessageFactory {

    @Override
    public MessageCreateData getTypeAdaptingFailedMessage(@NotNull SlashCommandContext context) {
        StringBuilder sbExpected = new StringBuilder();
        SlashCommandDefinition command = context.getCommand();
        List<String> arguments = Arrays.asList(context.getInput());

        command.getParameters().forEach(parameter -> {
            if (CommandEvent.class.isAssignableFrom(parameter.getType())) {
                return;
            }
            String typeName = parameter.getType().getTypeName();
            if (typeName.contains(".")) {
                typeName = typeName.substring(typeName.lastIndexOf(".") + 1);
            }
            sbExpected.append(typeName).append(", ");
        });
        String expected = sbExpected.toString().isEmpty() ? " " : sbExpected.substring(0, sbExpected.length() - 2);

        StringBuilder sbActual = new StringBuilder();
        arguments.forEach(argument -> sbActual.append(argument).append(", "));
        String actual = sbActual.toString().isEmpty() ? " " : sbActual.substring(0, sbActual.length() - 2);

        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle("Syntax Error")
                .setDescription(command.getDisplayName())
                .addField("Expected", String.format("`%s`", expected), false)
                .addField("Actual", String.format("`%s`", actual), false)
                .build();

        return new MessageCreateBuilder().setEmbeds(embed).build();
    }

    @Override
    public MessageCreateData getInsufficientPermissionsMessage(@NotNull Context context) {
        StringBuilder sbPermissions = new StringBuilder();
        GenericInteractionDefinition interaction = context.getInteractionDefinition();
        interaction.getPermissions().forEach(permission -> sbPermissions.append(permission).append(", "));
        String permissions = sbPermissions.toString().isEmpty() ? "N/A" : sbPermissions.substring(0, sbPermissions.length() - 2);
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Insufficient Permissions")
                .setDescription(String.format("`%s` requires specific permissions to be executed",
                        interaction.getDisplayName()))
                .addField("Permissions:",
                        String.format("`%s`", permissions), false
                ).build();
        return new MessageCreateBuilder().setEmbeds(embed).build();
    }

    @Override
    public MessageCreateData getConstraintFailedMessage(@NotNull Context context, @NotNull ConstraintDefinition constraint) {
        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle("Parameter Error")
                .setDescription(String.format("```%s```", constraint.getMessage()))
                .build()
        ).build();
    }

    @Override
    public MessageCreateData getCooldownMessage(@NotNull Context context, long ms) {
        long secs = TimeUnit.MILLISECONDS.toSeconds(ms);
        long seconds = secs % 60;
        long minutes = (secs / 60) % 60;
        long hours = (secs / (60 * 60)) % 24;

        StringBuilder cooldown = new StringBuilder();
        if (hours > 0) {
            cooldown.append(hours).append(hours == 1 ? " hour" : " hours");
        }
        if (minutes > 0) {
            if (cooldown.length() > 0) {
                cooldown.append(" ");
            }
            cooldown.append(minutes).append(minutes == 1 ? " minute" : " minutes");
        }
        if (seconds > 0) {
            if (cooldown.length() > 0) {
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

    @Override
    public MessageCreateData getWrongChannelTypeMessage(@NotNull Context context) {
        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Wrong Channel Type")
                .setDescription("This command cannot be executed in this type of channels!")
                .build()
        ).build();
    }

    @Override
    public MessageCreateData getCommandExecutionFailedMessage(@NotNull Context context, @NotNull Throwable exception) {
        String error;

        error = String.format("```The user \"%s\" attempted to execute an \"%s\" interaction at %s, " +
                        "but a \"%s\" occurred. " +
                        "Please refer to the logs for further information.```",
                context.getEvent().getUser().toString(),
                context.getEvent().getInteraction().getType(),
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

    @Override
    public MessageCreateData getUnknownInteractionMessage(@NotNull Context context) {
        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Unknown Interaction")
                .setDescription("This interaction timed out and is no longer available!")
                .build()
        ).build();
    }
}
