package com.github.kaktushose.jda.commands.embeds.error;

import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.commands.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.commands.CommandEvent;
import com.github.kaktushose.jda.commands.embeds.EmbedCache;
import com.github.kaktushose.jda.commands.reflect.ConstraintDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.CommandDefinition;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Subtype of {@link DefaultErrorMessageFactory} that can load the embeds from an {@link EmbedCache}.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @see DefaultErrorMessageFactory
 * @see EmbedCache
 * @since 2.0.0
 */
public class JsonErrorMessageFactory extends DefaultErrorMessageFactory {

    private final EmbedCache embedCache;

    public JsonErrorMessageFactory(EmbedCache embedCache) {
        this.embedCache = embedCache;
    }

    @Override
    public MessageCreateData getTypeAdaptingFailedMessage(@NotNull CommandContext context) {
        if (!embedCache.containsEmbed("typeAdaptingFailed")) {
            return super.getTypeAdaptingFailedMessage(context);
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
                .injectValue("usage", command.getMetadata().getUsage().replaceAll("\\{prefix}", PREFIX))
                .injectValue("expected", expected)
                .injectValue("actual", actual)
                .toMessageCreateData();
    }

    @Override
    public MessageCreateData getInsufficientPermissionsMessage(@NotNull CommandContext context) {
        if (!embedCache.containsEmbed("insufficientPermissions")) {
            return super.getInsufficientPermissionsMessage(context);
        }

        GuildSettings settings = context.getSettings();
        CommandDefinition command = context.getCommand();
        StringBuilder sbPermissions = new StringBuilder();
        command.getPermissions().forEach(permission -> sbPermissions.append(permission).append(", "));
        String permissions = sbPermissions.toString().isEmpty() ? "N/A" : sbPermissions.substring(0, sbPermissions.length() - 2);

        return embedCache.getEmbed("insufficientPermissions")
                .injectValue("prefix", PREFIX)
                .injectValue("label", command.getLabel())
                .injectValue("permissions", permissions)
                .toMessageCreateData();
    }

    @Override
    public MessageCreateData getGuildMutedMessage(@NotNull GenericContext<?> context) {
        if (!embedCache.containsEmbed("guildMuted")) {
            return super.getGuildMutedMessage(context);
        }
        return embedCache.getEmbed("guildMuted").toMessageCreateData();
    }

    @Override
    public MessageCreateData getChannelMutedMessage(@NotNull GenericContext<?> context) {
        if (!embedCache.containsEmbed("channelMuted")) {
            return super.getChannelMutedMessage(context);
        }
        return embedCache.getEmbed("channelMuted").toMessageCreateData();
    }

    @Override
    public MessageCreateData getUserMutedMessage(@NotNull GenericContext<?> context) {
        if (!embedCache.containsEmbed("userMuted")) {
            return super.getUserMutedMessage(context);
        }
        return embedCache.getEmbed("userMuted").toMessageCreateData();
    }

    @Override
    public MessageCreateData getConstraintFailedMessage(@NotNull GenericContext<?> context, @NotNull ConstraintDefinition constraint) {
        if (!embedCache.containsEmbed("constraintFailed")) {
            return super.getConstraintFailedMessage(context, constraint);
        }
        return embedCache.getEmbed("constraintFailed")
                .injectValue("message", constraint.getMessage())
                .toMessageCreateData();
    }

    @Override
    public MessageCreateData getCooldownMessage(@NotNull GenericContext<?> context, long ms) {
        if (!embedCache.containsEmbed("cooldown")) {
            return super.getCooldownMessage(context, ms);
        }
        long seconds = TimeUnit.MILLISECONDS.toSeconds(ms);
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        String cooldown = String.format("%d:%02d:%02d", h, m, s);

        return embedCache.getEmbed("cooldown")
                .injectValue("cooldown", cooldown)
                .toMessageCreateData();
    }

    @Override
    public MessageCreateData getWrongChannelTypeMessage(@NotNull GenericContext<?> context) {
        if (!embedCache.containsEmbed("wrongChannel")) {
            return super.getWrongChannelTypeMessage(context);
        }
        return embedCache.getEmbed("wrongChannel").toMessageCreateData();
    }

    @Override
    public MessageCreateData getCommandExecutionFailedMessage(@NotNull GenericContext<?> context, @NotNull Throwable exception) {
        if (!embedCache.containsEmbed("executionFailed")) {
            return super.getCommandExecutionFailedMessage(context, exception);
        }
        return embedCache.getEmbed("executionFailed")
                .injectValue("exception", exception.toString())
                .toMessageCreateData();
    }

}
