package com.github.kaktushose.jda.commands.embeds.help;

import com.github.kaktushose.jda.commands.data.CommandList;
import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.commands.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.filter.Filter;
import com.github.kaktushose.jda.commands.dispatching.filter.FilterRegistry;
import com.github.kaktushose.jda.commands.reflect.CommandMetadata;
import com.github.kaktushose.jda.commands.reflect.ControllerDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.CommandDefinition;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
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
    protected static final String PREFIX = Matcher.quoteReplacement("/");

    @Override
    public MessageCreateData getSpecificHelp(@NotNull CommandContext context) {
        EmbedBuilder builder = new EmbedBuilder();
        CommandDefinition command = context.getCommand();
        CommandMetadata metadata = command.getMetadata();

        StringBuilder sbPermissions = new StringBuilder();
        command.getPermissions().forEach(perm -> sbPermissions.append(perm).append(", "));
        String permissions = sbPermissions.toString().isEmpty() ? "N/A" : sbPermissions.substring(0, sbPermissions.length() - 2);

        builder.setColor(Color.GREEN)
                .setTitle("Specific Help")
                .setDescription(String.format("Command Details for `%s%s`", PREFIX, command.getLabel()))
                .addField("Name:", String.format("`%s`", metadata.getName().replaceAll(prefixPattern, PREFIX)), false)
                .addField("Usage:", String.format("`%s`", metadata.getUsage().replaceAll(prefixPattern, PREFIX)), false)
                .addField("Description:", String.format("`%s`", metadata.getDescription().replaceAll(prefixPattern, PREFIX)), false)
                .addField("Permissions:", String.format("`%s`", permissions), false)
                .addField("Category:", String.format("`%s`", metadata.getCategory().replaceAll(prefixPattern, PREFIX)), false);

        return new MessageCreateBuilder().setEmbeds(builder.build()).build();
    }

    /**
     * Gets a {@link Message} to send to get an overview over all available commands. Checks all {@link Filter Filters}
     * defined in phase
     * {@link com.github.kaktushose.jda.commands.dispatching.filter.FilterRegistry.FilterPosition#BEFORE_ADAPTING
     * FilterPosition#BEFORE_ADAPTING} to only display commands a user is allowed to execute.
     *
     * @param controllers a {@link Set} of all available {@link ControllerDefinition ControllerDefinitions}
     * @param context     context the corresponding {@link GenericContext}
     * @return a {@link Message} to send to get an overview over all available commands
     */
    @Override
    public MessageCreateData getGenericHelp(@NotNull Set<ControllerDefinition> controllers, @NotNull CommandContext context) {
        GuildSettings settings = context.getSettings();
        EmbedBuilder builder = new EmbedBuilder();
        CommandList commandList = new CommandList();
        controllers.forEach(definition -> commandList.addAll(definition.getCommands()));

        builder.setColor(Color.GREEN)
                .setTitle("General Help")
                .setDescription(String.format("The following commands are available. Type `%s%s <command>` to get specific help",
                        PREFIX,
                        settings.getHelpLabel()));

        List<Filter> filters = context.getJdaCommands().getFilterRegistry().getAll(FilterRegistry.FilterPosition.BEFORE_ADAPTING);
        for (CommandDefinition command : commandList) {
            context.setCommand(command);
            for (Filter filter : filters) {
                filter.apply(context);
                if (context.isCancelled()) {
                    commandList.remove(command);
                    break;
                }
            }
        }

        commandList.getSortedByCategories().forEach((category, commands) -> {
            StringBuilder sb = new StringBuilder();
            commands.forEach(command -> sb.append(String.format("`%s`", command.getLabel())).append(", "));
            builder.addField(category, sb.substring(0, sb.length() - 2), false);
        });

        builder.setFooter("This only displays commands that you are allowed to execute");

        return new MessageCreateBuilder().setEmbeds(builder.build()).build();
    }
}
