package com.github.kaktushose.jda.commands.embeds.error;

import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.reflect.ConstraintDefinition;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of {@link ErrorMessageFactory} with default embeds.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see JsonErrorMessageFactory
 * @since 2.0.0
 */
public class DefaultErrorMessageFactory implements ErrorMessageFactory {

    @Override
    public MessageCreateData getInsufficientPermissionsMessage(@NotNull GenericContext context) {
        GuildSettings settings = context.getSettings();
        StringBuilder sbPermissions = new StringBuilder();
        String permissions = sbPermissions.toString().isEmpty() ? "N/A" : sbPermissions.substring(0, sbPermissions.length() - 2);
        // TODO fix this
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Insufficient Permissions")
                .setDescription(String.format("`%s%s` requires specific permissions to be executed",
                        settings.getPrefix(),
                        "command.getLabel()"))
                .addField("Permissions:",
                        String.format("`%s`", permissions), false
                ).build();
        return new MessageCreateBuilder().setEmbeds(embed).build();
    }

    @Override
    public MessageCreateData getGuildMutedMessage(@NotNull GenericContext context) {
        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Insufficient Permissions")
                .setDescription("This guild is muted!")
                .build()
        ).build();
    }

    @Override
    public MessageCreateData getChannelMutedMessage(@NotNull GenericContext context) {
        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Insufficient Permissions")
                .setDescription("This channel is muted!")
                .build()
        ).build();
    }

    @Override
    public MessageCreateData getUserMutedMessage(@NotNull GenericContext context) {
        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Insufficient Permissions")
                .setDescription("You are muted!")
                .build()
        ).build();
    }

    @Override
    public MessageCreateData getConstraintFailedMessage(@NotNull GenericContext context, @NotNull ConstraintDefinition constraint) {
        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle("Parameter Error")
                .setDescription(String.format("```%s```", constraint.getMessage()))
                .build()
        ).build();
    }

    @Override
    public MessageCreateData getCooldownMessage(@NotNull GenericContext context, long ms) {
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
    public MessageCreateData getWrongChannelTypeMessage(@NotNull GenericContext context) {
        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Wrong Channel Type")
                .setDescription("This command cannot be executed in this type of channel!")
                .build()
        ).build();
    }

    @Override
    public MessageCreateData getCommandExecutionFailedMessage(@NotNull GenericContext context, @NotNull Exception exception) {
        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Command Execution Failed")
                .setDescription(String.format("```%s```", exception))
                .build()
        ).build();
    }

}
