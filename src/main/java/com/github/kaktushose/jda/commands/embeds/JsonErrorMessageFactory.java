package com.github.kaktushose.jda.commands.embeds;

import com.github.kaktushose.jda.commands.data.EmbedCache;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.SlashCommandContext;
import com.github.kaktushose.jda.commands.reflect.ConstraintDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.SlashCommandDefinition;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Subtype of {@link DefaultErrorMessageFactory} that can load the embeds from an {@link EmbedCache}.
 *
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
    public MessageCreateData getTypeAdaptingFailedMessage(@NotNull SlashCommandContext context) {
        if (!embedCache.containsEmbed("typeAdaptingFailed")) {
            return super.getTypeAdaptingFailedMessage(context);
        }

        StringBuilder sbExpected = new StringBuilder();
        SlashCommandDefinition command = context.getCommand();
        List<String> arguments = Arrays.asList(context.getInput());

        command.getParameters().forEach(parameter -> {
            if (CommandEvent.class.isAssignableFrom(parameter.type())) {
                return;
            }
            String typeName = parameter.type().getTypeName();
            if (typeName.contains(".")) {
                typeName = typeName.substring(typeName.lastIndexOf(".") + 1);
            }
            sbExpected.append(typeName).append(", ");
        });
        String expected = sbExpected.toString().isEmpty() ? " " : sbExpected.substring(0, sbExpected.length() - 2);

        StringBuilder sbActual = new StringBuilder();
        arguments.forEach(argument -> sbActual.append(argument).append(", "));
        String actual = sbActual.toString().isEmpty() ? " " : sbActual.substring(0, sbActual.length() - 2);

        return embedCache.getEmbed("typeAdaptingFailed")
                .injectValue("usage", command.getDisplayName())
                .injectValue("expected", expected)
                .injectValue("actual", actual)
                .toMessageCreateData();
    }

    @Override
    public MessageCreateData getInsufficientPermissionsMessage(@NotNull Context context) {
        if (!embedCache.containsEmbed("insufficientPermissions")) {
            return super.getInsufficientPermissionsMessage(context);
        }

        GenericInteractionDefinition interaction = context.getInteractionDefinition();
        StringBuilder sbPermissions = new StringBuilder();
        interaction.getPermissions().forEach(permission -> sbPermissions.append(permission).append(", "));
        String permissions = sbPermissions.toString().isEmpty() ? "N/A" : sbPermissions.substring(0, sbPermissions.length() - 2);

        return embedCache.getEmbed("insufficientPermissions")
                .injectValue("name", interaction.getDisplayName())
                .injectValue("permissions", permissions)
                .toMessageCreateData();
    }

    @Override
    public MessageCreateData getConstraintFailedMessage(@NotNull Context context, @NotNull ConstraintDefinition constraint) {
        if (!embedCache.containsEmbed("constraintFailed")) {
            return super.getConstraintFailedMessage(context, constraint);
        }
        return embedCache.getEmbed("constraintFailed")
                .injectValue("message", constraint.message())
                .toMessageCreateData();
    }

    @Override
    public MessageCreateData getCooldownMessage(@NotNull Context context, long ms) {
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
    public MessageCreateData getWrongChannelTypeMessage(@NotNull Context context) {
        if (!embedCache.containsEmbed("wrongChannel")) {
            return super.getWrongChannelTypeMessage(context);
        }
        return embedCache.getEmbed("wrongChannel").toMessageCreateData();
    }

    @Override
    public MessageCreateData getCommandExecutionFailedMessage(@NotNull Context context, @NotNull Throwable exception) {
        if (!embedCache.containsEmbed("executionFailed")) {
            return super.getCommandExecutionFailedMessage(context, exception);
        }
        String error = String.format("```The user \"%s\" attempted to execute an \"%s\" interaction at %s, " +
                              "but a \"%s\" occurred. " +
                              "Please refer to the logs for further information.```",
                context.getEvent().getUser().toString(),
                context.getEvent().getInteraction().getType(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()),
                exception.getClass().getName()
        );
        return embedCache.getEmbed("executionFailed")
                .injectValue("error", error)
                .toMessageCreateData();
    }

    @Override
    public MessageCreateData getUnknownInteractionMessage(@NotNull Context context) {
        if (!embedCache.containsEmbed("unknownInteraction")) {
            return super.getUnknownInteractionMessage(context);
        }
        return embedCache.getEmbed("unknownInteraction").toMessageCreateData();
    }
}
