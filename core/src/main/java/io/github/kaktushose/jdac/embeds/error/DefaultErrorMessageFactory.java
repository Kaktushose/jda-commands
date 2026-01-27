package io.github.kaktushose.jdac.embeds.error;

import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.definitions.interactions.command.OptionDataDefinition;
import io.github.kaktushose.jdac.definitions.interactions.command.SlashCommandDefinition;
import io.github.kaktushose.jdac.internal.Helpers;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import io.github.kaktushose.jdac.message.resolver.ComponentResolver;
import io.github.kaktushose.jdac.message.resolver.MessageResolver;
import io.github.kaktushose.proteus.conversion.ConversionResult;
import io.github.kaktushose.proteus.type.Type;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

/// The default implementation of [ErrorMessageFactory] using Components V2.
///
/// # Localization
/// For simple localization of these error messages you can add a `jdac_LOCALE.ftl` file to the resources folder and
/// provide the keys/ use the variables as listed below.
///
/// ## Type Adapting Failed
/// ### Keys
/// - `adapting-failed-title`
/// - `adapting-failed-details`
/// - `adapting-failed-message`
/// ### Variables
/// - `command`: The full command with parameter names, with the failed argument underlined, for instance: **/example** arg1 <u>arg2</u>
/// - `expected`: The expected argument type
/// - `actual`: The provided argument type
/// - `raw`: The raw, textual user input
/// - `message`: The error message of the type adapter
///
/// ## Insufficient Permissions
/// ### Keys
/// - `insufficient-permissions`
/// ### Variables
/// - `interaction`: The name of the interaction that failed
/// - `permissions`: The permissions that are required
/// ## Constraint Failed
/// ### Keys
/// - `constraint-failed`
/// ### Variables
/// - `message`: The already localized error message of the failed constraint
///
/// ## Interaction Execution Failed
/// ### Keys
/// - `execution-failed-title`
/// - `execution-failed-message`
/// ### Variables
/// - `user`: the user executing the interaction
/// - `interaction`: the interaction type
/// - `timestamp`: the current timestamp
/// - `exception`: the name of the exception class
///
/// ## Unknown Interaction
/// ### Keys
/// - `unknown-interaction`
///
/// # Own Implementation
/// Alternatively, you can pass your own [ErrorMessageFactory] implementation to
/// [JDACBuilder#errorMessageFactory(ErrorMessageFactory)].
public class DefaultErrorMessageFactory implements ErrorMessageFactory {

    private final ComponentResolver<Container> resolver;

    public DefaultErrorMessageFactory(MessageResolver resolver) {
        this.resolver = new ComponentResolver<>(resolver, Container.class);
    }

    /// {@inheritDoc}
    @Override
    public MessageTopLevelComponent getTypeAdaptingFailedMessage(ErrorContext context, ConversionResult.Failure<?> failure) {
        SlashCommandDefinition command = (SlashCommandDefinition) context.definition();
        SlashCommandInteractionEvent event = (SlashCommandInteractionEvent) context.event();
        List<OptionDataDefinition> commandOptions = new ArrayList<>(command.commandOptions());
        List<Optional<OptionMapping>> optionMappings = commandOptions
                .stream()
                .map(it -> Optional.ofNullable(event.getOption(it.name())))
                .toList();

        String name = "**%s**".formatted(command.displayName());
        String expected = "N/A";
        String actual = "N/A";
        String raw = "N/A";
        for (int i = 0; i < commandOptions.size(); i++) {
            OptionDataDefinition commandOption = commandOptions.get(i);
            Optional<OptionMapping> optionMapping = optionMappings.get(i);
            Type<?> into = Type.of(commandOption.declaredType());
            if (failure.context() != null && into.equals(failure.context().into())) {
                name = "%s __%s__".formatted(name, commandOption.name());
                name = "%s %s".formatted(name, commandOptions.subList(i + 1, commandOptions.size())
                        .stream()
                        .map(OptionDataDefinition::name)
                        .collect(Collectors.joining(" ")));
                expected = commandOption.declaredType().getSimpleName();
                actual = optionMapping.map(Helpers::humanReadableType).orElse("null");
                raw = optionMapping.map(OptionMapping::getAsString).orElse("null");
                break;
            } else {
                name = "%s %s".formatted(name, commandOption.name());
            }
        }

        return build(
                Container.of(
                        TextDisplay.of("jdac$adapting-failed-title"),
                        Separator.createDivider(Separator.Spacing.SMALL),
                        TextDisplay.of("jdac$adapting-failed-details"),
                        Separator.createDivider(Separator.Spacing.SMALL),
                        TextDisplay.of("jdac$adapting-failed-message")
                ),
                Color.ORANGE,
                context.event().getUserLocale(),
                entry("command", name),
                entry("expected", expected),
                entry("actual", actual),
                entry("raw", raw),
                entry("message", failure.message())
        );
    }

    /// {@inheritDoc}
    @Override
    public MessageTopLevelComponent getInsufficientPermissionsMessage(ErrorContext context) {
        StringBuilder sbPermissions = new StringBuilder();
        context.definition().permissions().forEach(permission -> sbPermissions.append(permission).append(", "));
        String permissions = sbPermissions.toString().isEmpty() ? "N/A" : sbPermissions.substring(0, sbPermissions.length() - 2);

        return build(
                Container.of(TextDisplay.of("jdac$insufficient-permissions")),
                Color.RED,
                context.event().getUserLocale(),
                entry("interaction", context.definition().displayName()),
                entry("permissions", permissions)
        );
    }

    /// {@inheritDoc}
    @Override
    public MessageTopLevelComponent getConstraintFailedMessage(ErrorContext context, String message) {
        return build(Container.of(
                        TextDisplay.of("jdac$constraint-failed")),
                Color.ORANGE,
                context.event().getUserLocale(),
                entry("message", message)
        );
    }

    /// {@inheritDoc}
    @Override
    public MessageTopLevelComponent getInteractionExecutionFailedMessage(ErrorContext context, Throwable exception) {
        return build(
                Container.of(
                        TextDisplay.of("jdac$execution-failed-title"),
                        Separator.createDivider(Separator.Spacing.SMALL),
                        TextDisplay.of("jdac$execution-failed-message")
                ),
                Color.RED,
                context.event().getUserLocale(),
                entry("user", context.event().getUser().getEffectiveName()),
                entry("interaction", context.event().getInteraction().getType()),
                entry("timestamp", LocalDateTime.now()),
                entry("exception", exception.getClass().getSimpleName())
        );
    }

    /// {@inheritDoc}
    @Override
    public MessageTopLevelComponent getTimedOutComponentMessage(GenericInteractionCreateEvent event) {
        return build(Container.of(TextDisplay.of("jdac$unknown-interaction")), Color.RED, event.getUserLocale());
    }

    private MessageTopLevelComponent build(Container container, Color color, DiscordLocale locale, Entry... placeholders) {
        return resolver.resolve(
                container.withAccentColor(color),
                locale,
                placeholders
        );
    }
}
