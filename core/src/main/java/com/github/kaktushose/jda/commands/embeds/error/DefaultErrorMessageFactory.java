package com.github.kaktushose.jda.commands.embeds.error;

import com.github.kaktushose.jda.commands.definitions.interactions.command.OptionDataDefinition.ConstraintDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.embeds.EmbedConfiguration;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/// The default implementation of [ErrorMessageFactory]. Supports loading the embeds from an [EmbedConfiguration].
public final class DefaultErrorMessageFactory extends BuilderErrorMessageFactory {

    private final EmbedConfiguration embedConfiguration;

    public DefaultErrorMessageFactory(@NotNull EmbedConfiguration embedConfiguration) {
        this.embedConfiguration = embedConfiguration;
    }

    @NotNull
    @Override
    public MessageCreateData getTypeAdaptingFailedMessage(@NotNull ErrorContext context, @NotNull List<String> userInput) {
        if (check("typeAdaptingFailed")) {
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

        return embedConfiguration.get("typeAdaptingFailed")
                .placeholder("usage", command.displayName())
                .placeholder("expected", expected)
                .placeholder("actual", actual)
                .toMessageCreateData();
    }

    @NotNull
    @Override
    public MessageCreateData getInsufficientPermissionsMessage(@NotNull ErrorContext context) {
        if (check("insufficientPermissions")) {
            return super.getInsufficientPermissionsMessage(context);
        }

        StringBuilder sbPermissions = new StringBuilder();
        context.definition().permissions().forEach(permission -> sbPermissions.append(permission).append(", "));
        String permissions = sbPermissions.toString().isEmpty() ? "N/A" : sbPermissions.substring(0, sbPermissions.length() - 2);

        return embedConfiguration.get("insufficientPermissions")
                .placeholder("name", context.definition().displayName())
                .placeholder("permissions", permissions)
                .toMessageCreateData();
    }

    @NotNull
    @Override
    public MessageCreateData getConstraintFailedMessage(@NotNull ErrorContext context, @NotNull ConstraintDefinition constraint) {
        if (check("constraintFailed")) {
            return super.getConstraintFailedMessage(context, constraint);
        }
        return embedConfiguration.get("constraintFailed")
                .placeholder("message", constraint.message())
                .toMessageCreateData();
    }

    @NotNull
    @Override
    public MessageCreateData getCooldownMessage(@NotNull ErrorContext context, long ms) {
        if (check("cooldown")) {
            return super.getCooldownMessage(context, ms);
        }
        long seconds = TimeUnit.MILLISECONDS.toSeconds(ms);
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        String cooldown = String.format("%d:%02d:%02d", h, m, s);

        return embedConfiguration.get("cooldown")
                .placeholder("cooldown", cooldown)
                .toMessageCreateData();
    }

    @NotNull
    @Override
    public MessageCreateData getCommandExecutionFailedMessage(@NotNull ErrorContext context, @NotNull Throwable exception) {
        if (check("executionFailed")) {
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
        return embedConfiguration.get("executionFailed")
                .placeholder("error", error)
                .toMessageCreateData();
    }

    @NotNull
    @Override
    public MessageCreateData getTimedOutComponentMessage(@NotNull GenericInteractionCreateEvent event) {
        if (check("unknownInteraction")) {
            return super.getTimedOutComponentMessage(event);
        }
        return embedConfiguration.get("unknownInteraction").toMessageCreateData();
    }

    private boolean check(String name) {
        return embedConfiguration.sources().stream()
                .map(source -> source.get(name, embedConfiguration.placeholders()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny()
                .isEmpty();
    }
}
