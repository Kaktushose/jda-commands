package com.github.kaktushose.jda.commands.embeds.error;

import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.commands.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.commands.CommandEvent;
import com.github.kaktushose.jda.commands.reflect.ConstraintDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.CommandDefinition;
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
import java.util.regex.Matcher;

/**
 * Implementation of {@link ErrorMessageFactory} with default embeds.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @see JsonErrorMessageFactory
 * @since 2.0.0
 */
public class DefaultErrorMessageFactory implements ErrorMessageFactory {

    protected static final String PREFIX = Matcher.quoteReplacement("/");

    @Override
    public MessageCreateData getTypeAdaptingFailedMessage(@NotNull CommandContext context) {
        StringBuilder sbExpected = new StringBuilder();
        CommandDefinition command = context.getCommand();
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
                .setDescription(String.format("%s%s", PREFIX, command.getName()))
                .addField("Expected", String.format("`%s`", expected), false)
                .addField("Actual", String.format("`%s`", actual), false)
                .build();

        return new MessageCreateBuilder().setEmbeds(embed).build();
    }

    @Override
    public MessageCreateData getInsufficientPermissionsMessage(@NotNull CommandContext context) {
        StringBuilder sbPermissions = new StringBuilder();
        CommandDefinition command = context.getCommand();
        command.getPermissions().forEach(permission -> sbPermissions.append(permission).append(", "));
        String permissions = sbPermissions.toString().isEmpty() ? "N/A" : sbPermissions.substring(0, sbPermissions.length() - 2);
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Insufficient Permissions")
                .setDescription(String.format("`%s%s` requires specific permissions to be executed",
                        PREFIX,
                        command.getName()))
                .addField("Permissions:",
                        String.format("`%s`", permissions), false
                ).build();
        return new MessageCreateBuilder().setEmbeds(embed).build();
    }

    @Override
    public MessageCreateData getGuildMutedMessage(@NotNull GenericContext<?> context) {
        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Insufficient Permissions")
                .setDescription("This guild is muted!")
                .build()
        ).build();
    }

    @Override
    public MessageCreateData getChannelMutedMessage(@NotNull GenericContext<?> context) {
        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Insufficient Permissions")
                .setDescription("This channel is muted!")
                .build()
        ).build();
    }

    @Override
    public MessageCreateData getUserMutedMessage(@NotNull GenericContext<?> context) {
        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Insufficient Permissions")
                .setDescription("You are muted!")
                .build()
        ).build();
    }

    @Override
    public MessageCreateData getConstraintFailedMessage(@NotNull GenericContext<?> context, @NotNull ConstraintDefinition constraint) {
        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle("Parameter Error")
                .setDescription(String.format("```%s```", constraint.getMessage()))
                .build()
        ).build();
    }

    @Override
    public MessageCreateData getCooldownMessage(@NotNull GenericContext<?> context, long ms) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(ms);
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        String cooldown = String.format("%d:%02d:%02d", h, m, s);
        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Cooldown")
                .setDescription(String.format("You cannot use this command for %s!", cooldown))
                .build()
        ).build();
    }

    @Override
    public MessageCreateData getWrongChannelTypeMessage(@NotNull GenericContext<?> context) {
        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Wrong Channel Type")
                .setDescription("This command cannot be executed in this type of channels!")
                .build()
        ).build();
    }

    @Override
    public MessageCreateData getCommandExecutionFailedMessage(@NotNull GenericContext<?> context, @NotNull Throwable exception) {
        String error;
        if (context instanceof CommandContext) {
            CommandContext commandContext = (CommandContext) context;
            error = String.format("```The user \"%s\" attempted to execute the command \"%s\" at %s, " +
                            "but a \"%s\" occurred. " +
                            "Please refer to the logs for further information.```",
                    commandContext.getEvent().getUser().toString(),
                    commandContext.getEvent().getFullCommandName(),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()),
                    exception.getClass().getName()
            );
        } else {
            error = exception.getClass().getName();
        }
        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Command Execution Failed")
                .setDescription("The command execution has unexpectedly failed. Please report the following error to the bot devs.")
                .addField("Error Message", error, false)
                .build()
        ).build();
    }

}
