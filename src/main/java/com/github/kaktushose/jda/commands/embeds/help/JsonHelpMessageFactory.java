package com.github.kaktushose.jda.commands.embeds.help;

import com.github.kaktushose.jda.commands.data.CommandList;
import com.github.kaktushose.jda.commands.dispatching.commands.CommandContext;
import com.github.kaktushose.jda.commands.embeds.EmbedCache;
import com.github.kaktushose.jda.commands.reflect.CommandMetadata;
import com.github.kaktushose.jda.commands.reflect.ControllerDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.CommandDefinition;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Subtype of {@link DefaultHelpMessageFactory} that can load the embeds from an {@link EmbedCache}.
 *
 * @author Kaktushose
 * @version 4.0.0
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
    public MessageCreateData getSpecificHelp(@NotNull CommandContext context) {
        if (!embedCache.containsEmbed("specificHelp")) {
            return super.getSpecificHelp(context);
        }
        CommandDefinition command = context.getCommand();
        CommandMetadata metadata = command.getMetadata();

        StringBuilder sbPermissions = new StringBuilder();
        command.getPermissions().forEach(perm -> sbPermissions.append(perm).append(", "));
        String permissions = sbPermissions.toString().isEmpty() ? "N/A" : sbPermissions.substring(0, sbPermissions.length() - 2);

        EmbedBuilder builder = embedCache.getEmbed("specificHelp")
                .injectValue("prefix", PREFIX)
                .injectValue("label", command.getLabel())
                .injectValue("name", metadata.getName().replaceAll(prefixPattern, PREFIX))
                .injectValue("usage", metadata.getUsage().replaceAll(prefixPattern, PREFIX))
                .injectValue("description", metadata.getDescription().replaceAll(prefixPattern, PREFIX))
                .injectValue("permissions", permissions)
                .injectValue("category", metadata.getCategory().replaceAll(prefixPattern, PREFIX))
                .toEmbedBuilder();

        return new MessageCreateBuilder().setEmbeds(builder.build()).build();
    }

    @Override
    public MessageCreateData getGenericHelp(@NotNull Set<ControllerDefinition> controllers, @NotNull CommandContext context) {
        if (!embedCache.containsEmbed("genericHelp")) {
            return super.getGenericHelp(controllers, context);
        }

        GuildSettings settings = context.getSettings();
        CommandList commandList = new CommandList();
        controllers.forEach(definition -> commandList.addAll(definition.getCommands()));

        EmbedBuilder builder = embedCache.getEmbed("genericHelp")
                .injectValue("prefix", PREFIX)
                .injectValue("helpLabel", settings.getHelpLabel())
                .toEmbedBuilder();

        commandList.getSortedByCategories().forEach((category, commands) -> {
            StringBuilder sb = new StringBuilder();
            commands.forEach(command -> sb.append(String.format("`%s`", command.getLabel())).append(", "));
            builder.addField(category, sb.substring(0, sb.length() - 2), false);
        });

        return new MessageCreateBuilder().setEmbeds(builder.build()).build();
    }

}
