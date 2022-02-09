package com.github.kaktushose.jda.commands.embeds.error;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.CommandEvent;
import com.github.kaktushose.jda.commands.reflect.CommandDefinition;
import com.github.kaktushose.jda.commands.reflect.ConstraintDefinition;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

/**
 * Implementation of {@link ErrorMessageFactory} with default embeds.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see JsonErrorMessageFactory
 * @since 2.0.0
 */
public class DefaultErrorMessageFactory implements ErrorMessageFactory {

    @Override
    public Message getCommandNotFoundMessage(@NotNull CommandContext context) {
        GuildSettings settings = context.getSettings();
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle("Command Not Found")
                .setDescription(
                        String.format("```type %s%s to get a list of all available commands```",
                                settings.getPrefix(),
                                settings.getHelpLabels().stream().findFirst().orElse("help"))
                );
        if (!context.getPossibleCommands().isEmpty()) {
            StringBuilder sbPossible = new StringBuilder();
            context.getPossibleCommands().forEach(command ->
                    sbPossible.append(String.format("`%s`", command.getLabels().get(0))).append(", ")
            );
            embed.addField("Similar Commands", sbPossible.substring(0, sbPossible.length() - 2), false);
        }
        return new MessageBuilder().setEmbeds(embed.build()).build();
    }

    @Override
    public Message getInsufficientPermissionsMessage(@NotNull CommandContext context) {
        GuildSettings settings = context.getSettings();
        CommandDefinition command = context.getCommand();
        StringBuilder sbPermissions = new StringBuilder();
        command.getPermissions().forEach(permission -> sbPermissions.append(permission).append(", "));
        String permissions = sbPermissions.toString().isEmpty() ? "N/A" : sbPermissions.substring(0, sbPermissions.length() - 2);

        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Insufficient Permissions")
                .setDescription(String.format("`%s%s` requires specific permissions to be executed",
                        settings.getPrefix(),
                        command.getLabels().get(0)))
                .addField("Permissions:",
                        String.format("`%s`", permissions), false
                ).build();
        return new MessageBuilder().setEmbeds(embed).build();
    }

    @Override
    public Message getGuildMutedMessage(@NotNull CommandContext context) {
        return new MessageBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Insufficient Permissions")
                .setDescription("This guild is muted!")
                .build()
        ).build();
    }

    @Override
    public Message getChannelMutedMessage(@NotNull CommandContext context) {
        return new MessageBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Insufficient Permissions")
                .setDescription("This channel is muted!")
                .build()
        ).build();
    }

    @Override
    public Message getUserMutedMessage(@NotNull CommandContext context) {
        return new MessageBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Insufficient Permissions")
                .setDescription("You are muted!")
                .build()
        ).build();
    }

    @Override
    public Message getSyntaxErrorMessage(@NotNull CommandContext context) {
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
                .setDescription(String.format("`%s`", command.getMetadata().getUsage().replaceAll(
                        "\\{prefix}", Matcher.quoteReplacement(context.getSettings().getPrefix())))
                )
                .addField("Expected", String.format("`%s`", expected), false)
                .addField("Actual", String.format("`%s`", actual), false)
                .build();

        return new MessageBuilder().setEmbeds(embed).build();
    }

    @Override
    public Message getConstraintFailedMessage(@NotNull CommandContext context, @NotNull ConstraintDefinition constraint) {
        return new MessageBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle("Parameter Error")
                .setDescription(String.format("```%s```", constraint.getMessage()))
                .build()
        ).build();
    }

    @Override
    public Message getCooldownMessage(@NotNull CommandContext context, long ms) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(ms);
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        String cooldown = String.format("%d:%02d:%02d", h, m, s);
        return new MessageBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Cooldown")
                .setDescription(String.format("You cannot use this command for %s!", cooldown))
                .build()
        ).build();
    }

    @Override
    public Message getWrongChannelTypeMessage(@NotNull CommandContext context) {
        return new MessageBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Wrong Channel Type")
                .setDescription("This command cannot be executed in this type of channel!")
                .build()
        ).build();
    }

    @Override
    public Message getCommandExecutionFailedMessage(@NotNull CommandContext context, @NotNull Exception exception) {
        return new MessageBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Command Execution Failed")
                .setDescription(String.format("```%s```", exception))
                .build()
        ).build();
    }
}
