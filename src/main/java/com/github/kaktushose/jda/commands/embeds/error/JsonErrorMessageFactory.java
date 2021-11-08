package com.github.kaktushose.jda.commands.embeds.error;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.CommandEvent;
import com.github.kaktushose.jda.commands.embeds.EmbedCache;
import com.github.kaktushose.jda.commands.reflect.CommandDefinition;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import net.dv8tion.jda.api.entities.Message;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

public class JsonErrorMessageFactory extends DefaultErrorMessageFactory {

    private final EmbedCache embedCache;

    public JsonErrorMessageFactory(EmbedCache embedCache) {
        this.embedCache = embedCache;
    }

    @Override
    public Message getCommandNotFoundMessage(CommandContext context) {
        if (!embedCache.containsEmbed("commandNotFound")) {
            return super.getCommandNotFoundMessage(context);
        }

        GuildSettings settings = context.getSettings();
        return embedCache.getEmbed("commandNotFound")
                .injectValue("prefix", settings.getPrefix())
                .injectValue("helpLabel", settings.getHelpLabels().stream().findFirst().orElse("help"))
                .toMessage();
    }

    @Override
    public Message getInsufficientPermissionsMessage(CommandContext context) {
        if (!embedCache.containsEmbed("insufficientPermissions")) {
            return super.getInsufficientPermissionsMessage(context);
        }

        GuildSettings settings = context.getSettings();
        CommandDefinition command = context.getCommand();
        StringBuilder sbPermissions = new StringBuilder();
        command.getPermissions().forEach(permission -> sbPermissions.append(permission).append(", "));
        String permissions = sbPermissions.toString().isEmpty() ? "N/A" : sbPermissions.substring(0, sbPermissions.length() - 2);

        return embedCache.getEmbed("insufficientPermissions")
                .injectValue("prefix", settings.getPrefix())
                .injectValue("label", command.getLabels().get(0))
                .injectValue("permissions", permissions)
                .toMessage();
    }

    @Override
    public Message getGuildMutedMessage(CommandContext context) {
        if (!embedCache.containsEmbed("guildMuted")) {
            return super.getInsufficientPermissionsMessage(context);
        }
        return embedCache.getEmbed("guildMuted").toMessage();
    }

    @Override
    public Message getChannelMutedMessage(CommandContext context) {
        if (!embedCache.containsEmbed("channelMuted")) {
            return super.getInsufficientPermissionsMessage(context);
        }
        return embedCache.getEmbed("channelMuted").toMessage();
    }

    @Override
    public Message getSyntaxErrorMessage(CommandContext context) {
        if (!embedCache.containsEmbed("syntaxError")) {
            return super.getInsufficientPermissionsMessage(context);
        }
        StringBuilder sbExpected = new StringBuilder();
        CommandDefinition command = context.getCommand();
        List<String> arguments = Arrays.asList(context.getInput());

        command.getParameters().forEach(parameter -> {
            if (CommandEvent.class.isAssignableFrom(parameter.getType())) {
                return;
            }
            String typeName = parameter.getType().getTypeName();
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
                .injectValue("usage", command.getMetadata().getUsage().replaceAll("\\{prefix}",
                        Matcher.quoteReplacement(context.getSettings().getPrefix()))
                )
                .injectValue("expected", expected)
                .injectValue("actual", actual)
                .toMessage();
    }

    @Override
    public Message getCooldownMessage(CommandContext context, long ms) {
        if (!embedCache.containsEmbed("cooldown")) {
            return super.getInsufficientPermissionsMessage(context);
        }
        long seconds = TimeUnit.MILLISECONDS.toSeconds(ms);
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        String cooldown = String.format("%d:%02d:%02d", h, m, s);

        return embedCache.getEmbed("cooldown")
                .injectValue("cooldown", cooldown)
                .toMessage();
    }

    @Override
    public Message getWrongChannelTypeMessage(CommandContext context) {
        if (!embedCache.containsEmbed("wrongChannel")) {
            return super.getInsufficientPermissionsMessage(context);
        }
        return embedCache.getEmbed("wrongChannel").toMessage();
    }

}
