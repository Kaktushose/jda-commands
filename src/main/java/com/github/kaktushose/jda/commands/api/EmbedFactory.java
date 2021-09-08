package com.github.kaktushose.jda.commands.api;

import com.github.kaktushose.jda.commands.entities.CommandCallable;
import com.github.kaktushose.jda.commands.entities.CommandList;
import com.github.kaktushose.jda.commands.entities.CommandSettings;
import com.github.kaktushose.jda.commands.internal.ParameterType;
import com.github.kaktushose.jda.commands.internal.Patterns;
import com.github.kaktushose.jda.commands.rewrite.reflect.CommandDefinition;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * The default embed factory of this framework. An embed factory provides a bunch of embeds that are frequently needed.
 *
 * @author Kaktushose
 * @version 1.1.1
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
        String prefix = settings.getPrefix();
        Map<String, List<CommandCallable>> sortedCommands = commands.getSortedByCategories();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.GREEN)
                .setTitle("General Help")
                .setDescription(String.format("The following commands are available. Type `%s%s <command>` to get specific help",
                        settings.getPrefix(),
                        settings.getHelpLabels().stream().findFirst().orElse("help")));
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
        String prefix = Matcher.quoteReplacement(settings.getPrefix());
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
    public MessageEmbed getCommandNotFoundEmbed(@Nonnull CommandSettings settings, @Nonnull GuildMessageReceivedEvent event) {
        return new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle("Command Not Found")
                .setDescription(String.format("```type %s%s to get a list of all available commands```",
                        settings.getPrefix(),
                        settings.getHelpLabels().stream().findFirst().orElse("help"))
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
    public MessageEmbed getInsufficientPermissionsEmbed(@Nonnull CommandDefinition commandCallable, @Nonnull CommandSettings settings, @Nonnull GuildMessageReceivedEvent event) {
        String perm = commandCallable.getPermissions().stream()
                .map(permission -> {
                    final Matcher matcher = Patterns.getJDAPermissionPattern().matcher(permission);

                    if (matcher.matches()) {
                        return matcher.group(1).toUpperCase();
                    } else if (settings.getAllPermissionRoles().containsKey(permission)) {
                        Role role = event.getGuild().getRoleById(settings.getPermissionRole(permission));
                        if (role == null) return "Unknown role";
                        return role.getAsMention();
                    }

                    return permission;
                }).collect(Collectors.joining(", "));

        return new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Insufficient Permissions")
                .setDescription(String.format("`%s%s` requires specific permissions to be executed",
                        settings.getPrefix(),
                        commandCallable.getLabels().get(0)))
                .addField("Permissions:",
                        String.format("%s", perm), false
                ).build();
    }

    /**
     * Creates an embed that provides help if the user executing the command is muted.
     *
     * @param settings the {@link CommandSettings}
     * @param event    the corresponding {@code GuildMessageReceivedEvent}
     * @return the MessageEmbed to send
     */
    public MessageEmbed getUserMutedEmbed(@Nonnull CommandSettings settings, @Nonnull GuildMessageReceivedEvent event) {
        return new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Insufficient Permissions")
                .setDescription("You are muted")
                .build();
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
            String typeName = parameter.getParameterType();
            sbExpected.append(getHumanReadableTypeName(typeName)).append(", ");
        });
        String expected = sbExpected.toString().isEmpty() ? " " : sbExpected.substring(0, sbExpected.length() - 2);

        StringBuilder sbActual = new StringBuilder();
        arguments.forEach(argument -> sbActual.append(argument).append(", "));
        String actual = sbActual.toString().isEmpty() ? " " : sbActual.substring(0, sbActual.length() - 2);
        return new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle("Syntax Error")
                .setDescription(String.format("`%s`", commandCallable.getUsage().replaceAll("\\{prefix}", Matcher.quoteReplacement(settings.getPrefix()))))
                .addField("Expected", String.format("`%s`", expected), false)
                .addField("Actual", String.format("`%s`", actual), false)
                .build();
    }

    public String getHumanReadableTypeName(String typeName) {
        switch (ParameterType.getByName(typeName)) {
            case BYTE:
                return "Byte";
            case SHORT:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
                return "Number";
            case CHAR:
                return "Single Letter";
            case STRING:
                return "String";
            case ARRAY:
                return "Several Arguments";
            case BOOLEAN:
                return "True Or False";
            case MEMBER:
            case USER:
                return "Member Mention";
            case ROLE:
                return "Role Mention";
            case TEXTCHANNEL:
                return "Textchannel Mention";
            case UNKNOWN:
            default:
                return "Unknown";
        }
    }

}
