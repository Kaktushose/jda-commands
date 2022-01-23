package com.github.kaktushose.jda.commands.embeds.help;

import com.github.kaktushose.jda.commands.data.CommandList;
import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.embeds.EmbedCache;
import com.github.kaktushose.jda.commands.reflect.CommandDefinition;
import com.github.kaktushose.jda.commands.reflect.CommandMetadata;
import com.github.kaktushose.jda.commands.reflect.ControllerDefinition;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * Subtype of {@link DefaultHelpMessageFactory} that can load the embeds from an {@link EmbedCache}.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see DefaultHelpMessageFactory
 * @see EmbedCache
 * @since 2.0.0
 */
public class JsonHelpMessageFactory extends DefaultHelpMessageFactory {

    private final EmbedCache embedCache;

    public JsonHelpMessageFactory(EmbedCache embedCache) {
        this.embedCache = embedCache;
    }

    @Override
    public Message getSpecificHelp(@NotNull CommandContext context) {
        if (!embedCache.containsEmbed("specificHelp")) {
            return super.getSpecificHelp(context);
        }

        String prefix = Matcher.quoteReplacement(context.getSettings().getPrefix());
        CommandDefinition command = context.getCommand();
        CommandMetadata metadata = command.getMetadata();

        List<String> labels = command.getLabels();
        StringBuilder sbAliases = new StringBuilder();
        labels.subList(1, labels.size()).forEach(label -> sbAliases.append(label).append(", "));
        String aliases = sbAliases.toString().isEmpty() ? "N/A" : sbAliases.substring(0, sbAliases.length() - 2);

        StringBuilder sbPermissions = new StringBuilder();
        command.getPermissions().forEach(perm -> sbPermissions.append(perm).append(", "));
        String permissions = sbPermissions.toString().isEmpty() ? "N/A" : sbPermissions.substring(0, sbPermissions.length() - 2);

        EmbedBuilder builder = embedCache.getEmbed("specificHelp")
                .injectValue("prefix", prefix)
                .injectValue("label", command.getLabels().get(0))
                .injectValue("name", metadata.getName().replaceAll(prefixPattern, prefix))
                .injectValue("usage", metadata.getUsage().replaceAll(prefixPattern, prefix))
                .injectValue("aliases", aliases)
                .injectValue("description", metadata.getDescription().replaceAll(prefixPattern, prefix))
                .injectValue("permissions", permissions)
                .injectValue("category", metadata.getCategory().replaceAll(prefixPattern, prefix))
                .toEmbedBuilder();

        StringBuilder sbCommands = new StringBuilder();
        String name;
        if (command.isSuper()) {
            name = "Sub Commands:";
            List<CommandDefinition> commands = command.getController().getSubCommands().stream().sorted().collect(Collectors.toList());
            commands.forEach(definition -> sbCommands.append(String.format("`%s`", definition.getLabels().get(0))).append(", "));
        } else {
            name = "Super Command:";
            List<CommandDefinition> commands = command.getController().getSuperCommands().stream().sorted().collect(Collectors.toList());
            commands.forEach(definition -> sbCommands.append(String.format("`%s`", definition.getLabels().get(0))).append(", "));
        }
        String commands = sbCommands.toString().isEmpty() ? "N/A" : sbCommands.substring(0, sbCommands.length() - 2);
        builder.addField(name, commands, false);

        return new MessageBuilder().setEmbeds(builder.build()).build();
    }

    @Override
    public Message getGenericHelp(@NotNull Set<ControllerDefinition> controllers, @NotNull CommandContext context) {
        if (!embedCache.containsEmbed("genericHelp")) {
            return super.getGenericHelp(controllers, context);
        }

        GuildSettings settings = context.getSettings();
        CommandList superCommands = new CommandList();
        controllers.forEach(definition -> superCommands.addAll(definition.getSuperCommands()));

        EmbedBuilder builder = embedCache.getEmbed("genericHelp")
                .injectValue("prefix", settings.getPrefix())
                .injectValue("helpLabel", settings.getHelpLabels().stream().findFirst().orElse("help"))
                .toEmbedBuilder();

        superCommands.getSortedByCategories().forEach((category, commands) -> {
            StringBuilder sb = new StringBuilder();
            commands.forEach(command -> sb.append(String.format("`%s`", command.getLabels().get(0))).append(", "));
            builder.addField(category, sb.substring(0, sb.length() - 2), false);
        });

        return new MessageBuilder().setEmbeds(builder.build()).build();
    }

}
