package com.github.kaktushose.jda.commands.embeds.error;

import com.github.kaktushose.jda.commands.definitions.interactions.command.OptionDataDefinition.ConstraintDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.embeds.EmbedCache;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

/// Subtype of [DefaultErrorMessageFactory] that can load the embeds from an [EmbedCache].
///
/// @see DefaultErrorMessageFactory
/// @see EmbedCache
public class JsonErrorMessageFactory extends DefaultErrorMessageFactory {

    private final EmbedCache embedCache;

    public JsonErrorMessageFactory(EmbedCache embedCache) {
        this.embedCache = embedCache;
    }

    
    @Override
    public MessageCreateData getTypeAdaptingFailedMessage(ErrorContext context, List<String> userInput) {
        if (!embedCache.containsEmbed("typeAdaptingFailed")) {
            return super.getTypeAdaptingFailedMessage(context, userInput);
        }

        StringBuilder sbExpected = new StringBuilder();
        SlashCommandDefinition command = (SlashCommandDefinition) context.definition();

        command.commandOptions().forEach(parameter -> {
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
        userInput.forEach(argument -> sbActual.append(argument).append(", "));
        String actual = sbActual.toString().isEmpty() ? " " : sbActual.substring(0, sbActual.length() - 2);

        return embedCache.getEmbed("typeAdaptingFailed")
                .injectValue("usage", command.displayName())
                .injectValue("expected", expected)
                .injectValue("actual", actual)
                .toMessageCreateData();
    }

    
    @Override
    public MessageCreateData getInsufficientPermissionsMessage(ErrorContext context) {
        if (!embedCache.containsEmbed("insufficientPermissions")) {
            return super.getInsufficientPermissionsMessage(context);
        }

        StringBuilder sbPermissions = new StringBuilder();
        context.definition().permissions().forEach(permission -> sbPermissions.append(permission).append(", "));
        String permissions = sbPermissions.toString().isEmpty() ? "N/A" : sbPermissions.substring(0, sbPermissions.length() - 2);

        return embedCache.getEmbed("insufficientPermissions")
                .injectValue("name", context.definition().displayName())
                .injectValue("permissions", permissions)
                .toMessageCreateData();
    }

    
    @Override
    public MessageCreateData getConstraintFailedMessage(ErrorContext context, ConstraintDefinition constraint) {
        if (!embedCache.containsEmbed("constraintFailed")) {
            return super.getConstraintFailedMessage(context, constraint);
        }
        return embedCache.getEmbed("constraintFailed")
                .injectValue("message", constraint.message())
                .toMessageCreateData();
    }

    
    @Override
    public MessageCreateData getCooldownMessage(ErrorContext context, long ms) {
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
    public MessageCreateData getWrongChannelTypeMessage(ErrorContext context) {
        if (!embedCache.containsEmbed("wrongChannel")) {
            return super.getWrongChannelTypeMessage(context);
        }
        return embedCache.getEmbed("wrongChannel").toMessageCreateData();
    }

    
    @Override
    public MessageCreateData getCommandExecutionFailedMessage(ErrorContext context, Throwable exception) {
        if (!embedCache.containsEmbed("executionFailed")) {
            return super.getCommandExecutionFailedMessage(context, exception);
        }
        String error = String.format("```The user \"%s\" attempted to execute an \"%s\" interaction at %s, " +
                        "but a \"%s\" occurred. " +
                        "Please refer to the logs for further information.```",
                context.event().getUser(),
                context.event().getInteraction().getType(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()),
                exception.getClass().getName()
        );
        return embedCache.getEmbed("executionFailed")
                .injectValue("error", error)
                .toMessageCreateData();
    }

    
    @Override
    public MessageCreateData getTimedOutComponentMessage(GenericInteractionCreateEvent event) {
        if (!embedCache.containsEmbed("unknownInteraction")) {
            return super.getTimedOutComponentMessage(event);
        }
        return embedCache.getEmbed("unknownInteraction").toMessageCreateData();
    }
}
