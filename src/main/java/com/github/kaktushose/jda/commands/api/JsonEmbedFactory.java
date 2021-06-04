package com.github.kaktushose.jda.commands.api;

import com.github.kaktushose.jda.commands.entities.CommandCallable;
import com.github.kaktushose.jda.commands.entities.CommandList;
import com.github.kaktushose.jda.commands.entities.CommandSettings;
import com.github.kaktushose.jda.commands.internal.Patterns;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * This class is similar to {@link EmbedFactory} but it uses a {@link EmbedCache} to first deserialize the embeds from
 * json and then inject all needed values.
 *
 * @author Kaktushose
 * @version 1.1.1
 * @see com.github.kaktushose.jda.commands.api.EmbedFactory
 * @see EmbedCache
 * @since 1.1.0
 */
public class JsonEmbedFactory extends EmbedFactory {

    protected final EmbedCache embedCache;

    /**
     * Constructs a new JsonEmbedFactory.
     *
     * @param file the json to load the embeds from.
     */
    public JsonEmbedFactory(File file) {
        embedCache = new EmbedCache(file);
        embedCache.loadEmbedsToCache();
    }

    /**
     * Constructs a new JsonEmbedFactory.
     *
     * @param stream the json to load the embeds from.
     */
    public JsonEmbedFactory(InputStream stream) {
        embedCache = new EmbedCache(stream);
        embedCache.loadEmbedsToCache();
    }

    /**
     * Creates an embed that provides general help. The embed will list all commands sorted by categories without giving
     * any details on a specific command.
     *
     * @param commands the {@link CommandList} to build the embed from
     * @param settings the {@link CommandSettings} to get the prefix
     * @param event    the corresponding {@code GuildMessageReceivedEvent}
     * @return the MessageEmbed to send
     */
    @Override
    public MessageEmbed getDefaultHelpEmbed(@NotNull CommandList commands, @NotNull CommandSettings settings, @NotNull GuildMessageReceivedEvent event) {
        if (!embedCache.containsEmbed("defaultHelp")) {
            return super.getDefaultHelpEmbed(commands, settings, event);
        }
        String prefix = settings.getPrefix();
        EmbedBuilder embedBuilder = embedCache.getEmbed("defaultHelp")
                .injectValue("prefix", prefix)
                .injectValue("helpLabel", settings.getHelpLabels().stream().findFirst().orElse("help"))
                .toEmbedBuilder();
        Map<String, List<CommandCallable>> sortedCommands = commands.getSortedByCategories();
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
    @Override
    public MessageEmbed getSpecificHelpEmbed(@NotNull CommandCallable commandCallable, @NotNull CommandSettings settings, @NotNull GuildMessageReceivedEvent event) {
        if (!embedCache.containsEmbed("specificHelp")) {
            return super.getSpecificHelpEmbed(commandCallable, settings, event);
        }
        List<String> labels = commandCallable.getLabels();
        StringBuilder sbAliases = new StringBuilder();
        labels.subList(1, labels.size()).forEach(label -> sbAliases.append(label).append(", "));
        String aliases = sbAliases.toString().isEmpty() ? "N/A" : sbAliases.substring(0, sbAliases.length() - 2);

        String prefix = settings.getPrefix();

        StringBuilder sbPermissions = new StringBuilder();
        commandCallable.getPermissions().forEach(perm -> sbPermissions.append(perm).append(", "));
        String permissions = sbPermissions.toString().isEmpty() ? "N/A" : sbPermissions.substring(0, sbPermissions.length() - 2);

        return embedCache.getEmbed("specificHelp")
                .injectValue("prefix", prefix)
                .injectValue("label", commandCallable.getLabels().get(0))
                .injectValue("name", commandCallable.getName().replaceAll(prefixPattern, prefix))
                .injectValue("usage", commandCallable.getUsage().replaceAll(prefixPattern, prefix))
                .injectValue("aliases", aliases)
                .injectValue("description", commandCallable.getDescription().replaceAll(prefixPattern, prefix))
                .injectValue("permissions", permissions)
                .injectValue("category", commandCallable.getCategory().replaceAll(prefixPattern, prefix))
                .toMessageEmbed();
    }

    /**
     * Creates an embed that provides help if no command was found.
     *
     * @param settings the {@link CommandSettings} to get the prefix
     * @param event    the corresponding {@code GuildMessageReceivedEvent}
     * @return the MessageEmbed to send
     */
    @Override
    public MessageEmbed getCommandNotFoundEmbed(@NotNull CommandSettings settings, @NotNull GuildMessageReceivedEvent event) {
        if (!embedCache.containsEmbed("commandNotFound")) {
            return super.getCommandNotFoundEmbed(settings, event);
        }
        return embedCache.getEmbed("commandNotFound")
                .injectValue("prefix", settings.getPrefix())
                .injectValue("helpLabel", settings.getHelpLabels().stream().findFirst().orElse("help"))
                .toMessageEmbed();
    }

    /**
     * Creates an embed that provides help if the permissions to execute a command are insufficient.
     *
     * @param commandCallable the {@link CommandCallable} to build the embed from
     * @param settings        the {@link CommandSettings}
     * @param event           the corresponding {@code GuildMessageReceivedEvent}
     * @return the MessageEmbed to send
     */
    @Override
    public MessageEmbed getInsufficientPermissionsEmbed(@NotNull CommandCallable commandCallable, @NotNull CommandSettings settings, @NotNull GuildMessageReceivedEvent event) {
        if (!embedCache.containsEmbed("insufficientPermissions")) {
            return super.getInsufficientPermissionsEmbed(commandCallable, settings, event);
        }

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

        return embedCache.getEmbed("insufficientPermissions")
                .injectValue("prefix", settings.getPrefix())
                .injectValue("label", commandCallable.getLabels().get(0))
                .injectValue("permissions", commandCallable.getPermissions().isEmpty() ? "N/A" : perm)
                .toMessageEmbed();
    }

    /**
     * Creates an embed that provides help if the user executing the command is muted.
     *
     * @param settings        the {@link CommandSettings}
     * @param event           the corresponding {@code GuildMessageReceivedEvent}
     * @return the MessageEmbed to send
     */
    public MessageEmbed getUserMutedEmbed( @Nonnull CommandSettings settings, @Nonnull GuildMessageReceivedEvent event) {
        if (!embedCache.containsEmbed("userMuted")) {
            return super.getUserMutedEmbed(settings, event);
        }
        return embedCache.getEmbed("userMuted")
                .toMessageEmbed();
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
    @Override
    public MessageEmbed getSyntaxErrorEmbed(@NotNull CommandCallable commandCallable, @NotNull List<String> arguments, @NotNull CommandSettings settings, @NotNull GuildMessageReceivedEvent event) {
        if (!embedCache.containsEmbed("syntaxError")) {
            return super.getSyntaxErrorEmbed(commandCallable, arguments, settings, event);
        }
        StringBuilder sbExpected = new StringBuilder();
        commandCallable.getParameters().forEach(parameter -> {
            String typeName = parameter.getParameterType();
            if (typeName.contains(".")) {
                typeName = typeName.substring(typeName.lastIndexOf(".") + 1);
            }
            sbExpected.append(typeName).append(", ");
        });
        String expected = sbExpected.toString().isEmpty() ? " " : sbExpected.substring(0, sbExpected.length() - 2);

        StringBuilder sbActual = new StringBuilder();
        arguments.forEach(argument -> sbActual.append(argument).append(", "));
        String actual = sbActual.toString().isEmpty() ? " " : sbActual.substring(0, sbActual.length() - 2);

        return embedCache.getEmbed("syntaxError")
                .injectValue("usage", commandCallable.getUsage().replaceAll("\\{prefix}", Matcher.quoteReplacement(settings.getPrefix())))
                .injectValue("expected", expected)
                .injectValue("actual", actual)
                .toMessageEmbed();
    }
}
