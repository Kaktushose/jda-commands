package com.github.kaktushose.jda.commands.embeds.help;

import com.github.kaktushose.jda.commands.data.CommandList;
import com.github.kaktushose.jda.commands.dispatching.commands.CommandContext;
import com.github.kaktushose.jda.commands.reflect.CommandMetadata;
import com.github.kaktushose.jda.commands.reflect.ControllerDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.CommandDefinition;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * Implementation of {@link HelpMessageFactory} with default embeds.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see JsonHelpMessageFactory
 * @since 2.0.0
 */
public class DefaultHelpMessageFactory implements HelpMessageFactory {

    /**
     * The pattern that is used to insert prefixes. The default value is {@code {prefix}}.
     */
    protected String prefixPattern = "\\{prefix}";

    @Override
    public MessageCreateData getSpecificHelp(@NotNull CommandContext context) {
        String prefix = Matcher.quoteReplacement("/");
        EmbedBuilder builder = new EmbedBuilder();
        CommandDefinition command = context.getCommand();
        CommandMetadata metadata = command.getMetadata();

        StringBuilder sbPermissions = new StringBuilder();
        command.getPermissions().forEach(perm -> sbPermissions.append(perm).append(", "));
        String permissions = sbPermissions.toString().isEmpty() ? "N/A" : sbPermissions.substring(0, sbPermissions.length() - 2);

        builder.setColor(Color.GREEN)
                .setTitle("Specific Help")
                .setDescription(String.format("Command Details for `%s%s`", prefix, command.getLabel()))
                .addField("Name:", String.format("`%s`", metadata.getName().replaceAll(prefixPattern, prefix)), false)
                .addField("Usage:", String.format("`%s`", metadata.getUsage().replaceAll(prefixPattern, prefix)), false)
                .addField("Description:", String.format("`%s`", metadata.getDescription().replaceAll(prefixPattern, prefix)), false)
                .addField("Permissions:", String.format("`%s`", permissions), false)
                .addField("Category:", String.format("`%s`", metadata.getCategory().replaceAll(prefixPattern, prefix)), false);

        return new MessageCreateBuilder().setEmbeds(builder.build()).build();
    }

    @Override
    public MessageCreateData getGenericHelp(@NotNull Set<ControllerDefinition> controllers, @NotNull CommandContext context) {
        GuildSettings settings = context.getSettings();
        EmbedBuilder builder = new EmbedBuilder();
        CommandList commandList = new CommandList();
        controllers.forEach(definition -> commandList.addAll(definition.getCommands()));
        String prefix = Matcher.quoteReplacement("/");

        builder.setColor(Color.GREEN)
                .setTitle("General Help")
                .setDescription(String.format("The following commands are available. Type `%s%s <command>` to get specific help",
                        prefix,
                        settings.getHelpLabels().stream().findFirst().orElse("help")));

        commandList.getSortedByCategories().forEach((category, commands) -> {
            StringBuilder sb = new StringBuilder();
            commands.forEach(command -> sb.append(String.format("`%s`", command.getLabel())).append(", "));
            builder.addField(category, sb.substring(0, sb.length() - 2), false);
        });

        return new MessageCreateBuilder().setEmbeds(builder.build()).build();
    }
}
