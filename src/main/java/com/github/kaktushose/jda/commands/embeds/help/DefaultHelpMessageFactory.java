package com.github.kaktushose.jda.commands.embeds.help;

import com.github.kaktushose.jda.commands.data.CommandList;
import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.reflect.interactions.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.reflect.CommandMetadata;
import com.github.kaktushose.jda.commands.reflect.ControllerDefinition;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

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
    public Message getSpecificHelp(@NotNull CommandContext context) {
        String prefix = Matcher.quoteReplacement(context.getContextualPrefix());
        EmbedBuilder builder = new EmbedBuilder();
        SlashCommandDefinition command = context.getCommand();
        CommandMetadata metadata = command.getMetadata();

        List<String> labels = command.getLabel();
        StringBuilder sbAliases = new StringBuilder();
        labels.subList(1, labels.size()).forEach(label -> sbAliases.append(label).append(", "));
        String aliases = sbAliases.toString().isEmpty() ? "N/A" : sbAliases.substring(0, sbAliases.length() - 2);

        StringBuilder sbPermissions = new StringBuilder();
        command.getPermissions().forEach(perm -> sbPermissions.append(perm).append(", "));
        String permissions = sbPermissions.toString().isEmpty() ? "N/A" : sbPermissions.substring(0, sbPermissions.length() - 2);

        builder.setColor(Color.GREEN)
                .setTitle("Specific Help")
                .setDescription(String.format("Command Details for `%s%s`", prefix, command.getLabel().get(0)))
                .addField("Name:", String.format("`%s`", metadata.getName().replaceAll(prefixPattern, prefix)), false)
                .addField("Usage:", String.format("`%s`", metadata.getUsage().replaceAll(prefixPattern, prefix)), false)
                .addField("Aliases", String.format("`%s`", aliases), false)
                .addField("Description:", String.format("`%s`", metadata.getDescription().replaceAll(prefixPattern, prefix)), false)
                .addField("Permissions:", String.format("`%s`", permissions), false)
                .addField("Category:", String.format("`%s`", metadata.getCategory().replaceAll(prefixPattern, prefix)), false);

        StringBuilder sbCommands = new StringBuilder();
        String name;
        if (command.isSuper()) {
            name = "Sub Commands:";
            List<SlashCommandDefinition> commands = command.getController().getSubCommands().stream().sorted().collect(Collectors.toList());
            commands.forEach(definition -> sbCommands.append(String.format("`%s`", definition.getLabel().get(0))).append(", "));
        } else {
            name = "Super Command:";
            List<SlashCommandDefinition> commands = command.getController().getSuperCommands().stream().sorted().collect(Collectors.toList());
            commands.forEach(definition -> sbCommands.append(String.format("`%s`", definition.getLabel().get(0))).append(", "));
        }
        String commands = sbCommands.toString().isEmpty() ? "N/A" : sbCommands.substring(0, sbCommands.length() - 2);
        builder.addField(name, commands, false);

        return new MessageBuilder().setEmbeds(builder.build()).build();
    }

    @Override
    public Message getGenericHelp(@NotNull Set<ControllerDefinition> controllers, @NotNull CommandContext context) {
        GuildSettings settings = context.getSettings();
        EmbedBuilder builder = new EmbedBuilder();
        CommandList superCommands = new CommandList();
        controllers.forEach(definition -> superCommands.addAll(definition.getSuperCommands()));
        String prefix = Matcher.quoteReplacement(context.getContextualPrefix());

        builder.setColor(Color.GREEN)
                .setTitle("General Help")
                .setDescription(String.format("The following commands are available. Type `%s%s <command>` to get specific help",
                        prefix,
                        settings.getHelpLabels().stream().findFirst().orElse("help")));

        superCommands.getSortedByCategories().forEach((category, commands) -> {
            StringBuilder sb = new StringBuilder();
            commands.forEach(command -> sb.append(String.format("`%s`", command.getLabel().get(0))).append(", "));
            builder.addField(category, sb.substring(0, sb.length() - 2), false);
        });

        return new MessageBuilder().setEmbeds(builder.build()).build();
    }
}
