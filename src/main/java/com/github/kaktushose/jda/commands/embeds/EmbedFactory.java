package com.github.kaktushose.jda.commands.embeds;

import net.dv8tion.jda.api.entities.MessageEmbed;

public interface EmbedFactory {

    MessageEmbed getDefaultHelpEmbed();

    MessageEmbed getSpecificHelpEmbed();

    MessageEmbed getCommandNotFoundEmbed();

    MessageEmbed getInsufficientPermissionsEmbed();

    MessageEmbed getUserMutedEmbed();

    MessageEmbed getSyntaxErrorEmbed();

}
