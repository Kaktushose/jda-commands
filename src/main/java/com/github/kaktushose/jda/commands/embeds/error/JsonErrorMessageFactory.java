package com.github.kaktushose.jda.commands.embeds.error;

import com.github.kaktushose.jda.commands.definitions.ParameterDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.embeds.EmbedCache;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
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

    @NotNull
    @Override
    public MessageCreateData getTypeAdaptingFailedMessage(@NotNull GenericInteractionCreateEvent event,
                                                          @NotNull InteractionDefinition definition,
                                                          @NotNull List<String> userInput) {
        if (!embedCache.containsEmbed("typeAdaptingFailed")) {
            return super.getTypeAdaptingFailedMessage(event, definition, userInput);
        }

        StringBuilder sbExpected = new StringBuilder();
        SlashCommandDefinition command = (SlashCommandDefinition) definition;

        command.commandParameters().forEach(parameter -> {
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

    @NotNull
    @Override
    public MessageCreateData getInsufficientPermissionsMessage(@NotNull InteractionDefinition interaction) {
        if (!embedCache.containsEmbed("insufficientPermissions")) {
            return super.getInsufficientPermissionsMessage(interaction);
        }

        StringBuilder sbPermissions = new StringBuilder();
        interaction.permissions().forEach(permission -> sbPermissions.append(permission).append(", "));
        String permissions = sbPermissions.toString().isEmpty() ? "N/A" : sbPermissions.substring(0, sbPermissions.length() - 2);

        return embedCache.getEmbed("insufficientPermissions")
                .injectValue("name", interaction.displayName())
                .injectValue("permissions", permissions)
                .toMessageCreateData();
    }

    @NotNull
    @Override
    public MessageCreateData getConstraintFailedMessage(@NotNull ParameterDefinition.ConstraintDefinition constraint) {
        if (!embedCache.containsEmbed("constraintFailed")) {
            return super.getConstraintFailedMessage(constraint);
        }
        return embedCache.getEmbed("constraintFailed")
                .injectValue("message", constraint.message())
                .toMessageCreateData();
    }

    @NotNull
    @Override
    public MessageCreateData getCooldownMessage(long ms) {
        if (!embedCache.containsEmbed("cooldown")) {
            return super.getCooldownMessage(ms);
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

    @NotNull
    @Override
    public MessageCreateData getWrongChannelTypeMessage() {
        if (!embedCache.containsEmbed("wrongChannel")) {
            return super.getWrongChannelTypeMessage();
        }
        return embedCache.getEmbed("wrongChannel").toMessageCreateData();
    }

    @NotNull
    @Override
    public MessageCreateData getCommandExecutionFailedMessage(@NotNull GenericInteractionCreateEvent event, @NotNull Throwable exception) {
        if (!embedCache.containsEmbed("executionFailed")) {
            return super.getCommandExecutionFailedMessage(event, exception);
        }
        String error = String.format("```The user \"%s\" attempted to execute an \"%s\" interaction at %s, " +
                        "but a \"%s\" occurred. " +
                        "Please refer to the logs for further information.```",
                event.getUser(),
                event.getInteraction().getType(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()),
                exception.getClass().getName()
        );
        return embedCache.getEmbed("executionFailed")
                .injectValue("error", error)
                .toMessageCreateData();
    }

    @NotNull
    @Override
    public MessageCreateData getTimedOutComponentMessage() {
        if (!embedCache.containsEmbed("unknownInteraction")) {
            return super.getTimedOutComponentMessage();
        }
        return embedCache.getEmbed("unknownInteraction").toMessageCreateData();
    }
}
