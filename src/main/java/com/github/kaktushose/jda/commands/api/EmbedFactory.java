package com.github.kaktushose.jda.commands.api;

import com.github.kaktushose.jda.commands.entities.CommandCallable;
import com.github.kaktushose.jda.commands.entities.CommandList;
import com.github.kaktushose.jda.commands.entities.CommandSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The default embed factory of this framework. An embed factory provides a bunch of embeds that are frequently needed.
 *
 * @author Kaktushose
 * @version 1.0.0
 * @since 1.0.0
 */
public class EmbedFactory {

    /**
     * The pattern that is used to insert prefixes. The default value is {@code {prefix}}.
     */
    protected String prefixPattern = "\\{prefix}";

    /**
     * Creates an embed that provides general help. The embed will list all commands sorted by categories without giving
     * any details on a specific command.
     *
     * @param commands the {@link CommandList} to build the embed from
     * @param settings the {@link CommandSettings} to get the prefix
     * @param event    the corresponding {@code GuildMessageReceivedEvent}
     * @return the MessageEmbed to send
     */
    public MessageEmbed getDefaultHelpEmbed(@Nonnull CommandList commands, @Nonnull CommandSettings settings, @Nonnull GuildMessageReceivedEvent event) {
        String prefix = settings.getGuildPrefix(event.getGuild());
        Map<String, List<CommandCallable>> sortedCommands = commands.getSortedByCategories();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.GREEN)
                .setTitle("General Help")
                .setDescription(String.format("The following commands are available. Type `%shelp <command>` to get specific help",
                        settings.getGuildPrefix(event.getGuild())));
        sortedCommands.forEach((category, commandCallables) -> {
            StringBuilder sb = new StringBuilder();
            commandCallables.forEach(commandCallable -> sb.append(String.format("`%s%s`", prefix, commandCallable.getLabels().get(0))).append(", "));
            embedBuilder.addField(category, sb.substring(0, sb.length() - 2), false);
        });
        return embedBuilder.build();
    }

    /**
     * Creates an embed that provides help for a specific command.
     *
     * @param commandCallable the {@link CommandCallable} to build the embed from
     * @param settings        the {@link CommandSettings} to get the prefix
     * @param event           the corresponding {@code GuildMessageReceivedEvent}
     * @return the MessageEmbed to send
     */
    public MessageEmbed getSpecificHelpEmbed(@Nonnull CommandCallable commandCallable, @Nonnull CommandSettings settings, @Nonnull GuildMessageReceivedEvent event) {
        String prefix = Matcher.quoteReplacement(settings.getGuildPrefix(event.getGuild()));
        EmbedBuilder embedBuilder = new EmbedBuilder();

        List<String> labels = commandCallable.getLabels();
        StringBuilder sbAliases = new StringBuilder();
        labels.subList(1, labels.size()).forEach(label -> sbAliases.append(label).append(", "));
        String aliases = sbAliases.toString().isEmpty() ? "N/A" : sbAliases.substring(0, sbAliases.length() - 2);

        StringBuilder sbPermissions = new StringBuilder();
        commandCallable.getPermissions().forEach(perm -> sbPermissions.append(perm).append(", "));
        String permissions = sbPermissions.toString().isEmpty() ? "N/A" : sbPermissions.substring(0, sbPermissions.length() - 2);

        embedBuilder.setColor(Color.GREEN)
                .setTitle("Specific Help")
                .setDescription(String.format("Command Details for `%s%s`", prefix, commandCallable.getLabels().get(0)))
                .addField("Name:", String.format("`%s`", commandCallable.getName().replaceAll(prefixPattern, prefix)), false)
                .addField("Usage:", String.format("`%s`", commandCallable.getUsage().replaceAll(prefixPattern, prefix)), false)
                .addField("Aliases", String.format("`%s`", aliases), false)
                .addField("Description:", String.format("`%s`", commandCallable.getDescription().replaceAll(prefixPattern, prefix)), false)
                .addField("Permissions:", String.format("`%s`", permissions), false)
                .addField("Category:", String.format("`%s`", commandCallable.getCategory().replaceAll(prefixPattern, prefix)), false);
        return embedBuilder.build();
    }

    /**
     * Creates an embed that provides help if no command was found.
     *
     * @param settings the {@link CommandSettings} to get the prefix
     * @param event    the corresponding {@code GuildMessageReceivedEvent}
     * @return the MessageEmbed to send
     */
    public MessageEmbed getCommandNotFoundEmbed(@Nonnull CommandSettings settings,@Nonnull GuildMessageReceivedEvent event) {
        return new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle("Command Not Found")
                .setDescription(String.format("```type %shelp to get a list of all available commands```",
                        settings.getGuildPrefix(event.getGuild()))
                ).build();
    }

    /**
     * Creates an embed that provides help if the permissions to execute a command are insufficient.
     *
     * @param commandCallable the {@link CommandCallable} to build the embed from
     * @param settings        the {@link CommandSettings}
     * @param event           the corresponding {@code GuildMessageReceivedEvent}
     * @return the MessageEmbed to send
     */
    public MessageEmbed getInsufficientPermissionsEmbed(@Nonnull CommandCallable commandCallable, @Nonnull CommandSettings settings, @Nonnull GuildMessageReceivedEvent event) {
        StringBuilder sbPermissions = new StringBuilder();
        commandCallable.getPermissions().forEach(permission -> sbPermissions.append(permission).append(", "));
        String permissions = sbPermissions.toString().isEmpty() ? "N/A" : sbPermissions.substring(0, sbPermissions.length() - 2);

        return new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Insufficient Permissions")
                .setDescription(String.format("`%s%s` requires specific permissions to be executed",
                        settings.getGuildPrefix(event.getGuild()),
                        commandCallable.getLabels().get(0)))
                .addField("Permissions:",
                        String.format("`%s`", permissions), false
                ).build();
    }

    /**
     * Creates an embed that provides help if a command was found but the argument syntax was wrong.
     *
     * @param commandCallable the {@link CommandCallable} to build the embed from
     * @param arguments       the given arguments
     * @param settings        the {@link CommandSettings} to get the prefix
     * @param event           the corresponding {@code GuildMessageReceivedEvent}
     * @return the MessageEmbed to send
     */
    public MessageEmbed getSyntaxErrorEmbed(@Nonnull CommandCallable commandCallable,
                                            @Nonnull List<String> arguments,
                                            @Nonnull CommandSettings settings,
                                            @Nonnull GuildMessageReceivedEvent event) {
        StringBuilder sbExpected = new StringBuilder();
        commandCallable.getParameters().forEach(parameter -> {
            String typeName = parameter.getParameterType().name;
            if (typeName.contains(".")) {
                typeName = typeName.substring(typeName.lastIndexOf(".") + 1);
            }
            sbExpected.append(typeName).append(", ");
        });
        String expected = sbExpected.toString().isEmpty() ? " " : sbExpected.substring(0, sbExpected.length() - 2);

        StringBuilder sbActual = new StringBuilder();
        arguments.forEach(argument -> sbActual.append(argument).append(", "));
        String actual = sbActual.toString().isEmpty() ? " " : sbActual.substring(0, sbActual.length() - 2);

        return new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle("Syntax Error")
                .setDescription(String.format("`%s`", commandCallable.getUsage().replaceAll("\\{prefix}", Matcher.quoteReplacement(settings.getGuildPrefix(event.getGuild())))))
                .addField("Expected", String.format("`%s`", expected), false)
                .addField("Actual", String.format("`%s`", actual), false)
                .build();
    }

}