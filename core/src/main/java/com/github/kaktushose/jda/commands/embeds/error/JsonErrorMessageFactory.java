package com.github.kaktushose.jda.commands.embeds.error;

import com.github.kaktushose.jda.commands.definitions.interactions.command.OptionDataDefinition.ConstraintDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.embeds.Embeds;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

/// Subtype of [DefaultErrorMessageFactory] that can load the embeds from a json file.
///
/// @see DefaultErrorMessageFactory
/// @see Embeds
public class JsonErrorMessageFactory extends DefaultErrorMessageFactory {

    private final Embeds embeds;

    public JsonErrorMessageFactory(@NotNull Embeds embeds) {
        this.embeds = embeds;
    }

    @NotNull
    @Override
    public MessageCreateData getTypeAdaptingFailedMessage(@NotNull ErrorContext context, @NotNull List<String> userInput) {
        if (!embeds.exists("typeAdaptingFailed")) {
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

        return embeds.get("typeAdaptingFailed")
                .placeholder("usage", command.displayName())
                .placeholder("expected", expected)
                .placeholder("actual", actual)
                .toMessageCreateData();
    }

    @NotNull
    @Override
    public MessageCreateData getInsufficientPermissionsMessage(@NotNull ErrorContext context) {
        if (!embeds.exists("insufficientPermissions")) {
            return super.getInsufficientPermissionsMessage(context);
        }

        StringBuilder sbPermissions = new StringBuilder();
        context.definition().permissions().forEach(permission -> sbPermissions.append(permission).append(", "));
        String permissions = sbPermissions.toString().isEmpty() ? "N/A" : sbPermissions.substring(0, sbPermissions.length() - 2);

        return embeds.get("insufficientPermissions")
                .placeholder("name", context.definition().displayName())
                .placeholder("permissions", permissions)
                .toMessageCreateData();
    }

    @NotNull
    @Override
    public MessageCreateData getConstraintFailedMessage(@NotNull ErrorContext context, @NotNull ConstraintDefinition constraint) {
        if (!embeds.exists("constraintFailed")) {
            return super.getConstraintFailedMessage(context, constraint);
        }
        return embeds.get("constraintFailed")
                .placeholder("message", constraint.message())
                .toMessageCreateData();
    }

    @NotNull
    @Override
    public MessageCreateData getCooldownMessage(@NotNull ErrorContext context, long ms) {
        if (!embeds.exists("cooldown")) {
            return super.getCooldownMessage(context, ms);
        }
        long seconds = TimeUnit.MILLISECONDS.toSeconds(ms);
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        String cooldown = String.format("%d:%02d:%02d", h, m, s);

        return embeds.get("cooldown")
                .placeholder("cooldown", cooldown)
                .toMessageCreateData();
    }

    @NotNull
    @Override
    public MessageCreateData getCommandExecutionFailedMessage(@NotNull ErrorContext context, @NotNull Throwable exception) {
        if (!embeds.exists("executionFailed")) {
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
        return embeds.get("executionFailed")
                .placeholder("error", error)
                .toMessageCreateData();
    }

    @NotNull
    @Override
    public MessageCreateData getTimedOutComponentMessage(@NotNull GenericInteractionCreateEvent event) {
        if (!embeds.exists("unknownInteraction")) {
            return super.getTimedOutComponentMessage(event);
        }
        return embeds.get("unknownInteraction").toMessageCreateData();
    }
}
