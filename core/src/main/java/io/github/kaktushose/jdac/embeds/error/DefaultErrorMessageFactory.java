package io.github.kaktushose.jdac.embeds.error;

import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.definitions.interactions.command.OptionDataDefinition;
import io.github.kaktushose.jdac.definitions.interactions.command.SlashCommandDefinition;
import io.github.kaktushose.jdac.embeds.EmbedConfig;
import io.github.kaktushose.jdac.embeds.EmbedDataSource;
import io.github.kaktushose.jdac.internal.Helpers;
import io.github.kaktushose.jdac.message.ComponentResolver;
import io.github.kaktushose.jdac.message.MessageResolver;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import io.github.kaktushose.proteus.conversion.ConversionResult;
import io.github.kaktushose.proteus.type.Type;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

/// The default implementation of [ErrorMessageFactory] using Components V2.
public final class DefaultErrorMessageFactory implements ErrorMessageFactory {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final ComponentResolver<Container> resolver;

    public DefaultErrorMessageFactory(MessageResolver resolver) {
        this.resolver = new ComponentResolver<>(resolver, Container.class);
    }

    /// {@inheritDoc}
    /// Use [EmbedConfig#errorSource(EmbedDataSource)] to replace the default embed of this error message. Alternatively,
    /// pass your own [ErrorMessageFactory] implementation to [JDACBuilder#errorMessageFactory(ErrorMessageFactory)].
    @Override
    public MessageCreateData getTypeAdaptingFailedMessage(ErrorContext context, ConversionResult.Failure<?> failure) {
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
    /// Use [EmbedConfig#errorSource(EmbedDataSource)] to replace the default embed of this error message. Alternatively,
    /// pass your own [ErrorMessageFactory] implementation to [JDACBuilder#errorMessageFactory(ErrorMessageFactory)].
    @Override
    public MessageCreateData getInsufficientPermissionsMessage(ErrorContext context) {
        StringBuilder sbPermissions = new StringBuilder();
        context.definition().permissions().forEach(permission -> sbPermissions.append(permission).append(", "));
        String permissions = sbPermissions.toString().isEmpty() ? "N/A" : sbPermissions.substring(0, sbPermissions.length() - 2);

        return build(
                Container.of(TextDisplay.of("jdac$insufficient-permissions")),
                Color.RED,
                context.event().getUserLocale(),
                entry("command", context.definition().displayName()),
                entry("permissions", permissions)
        );
    }

    /// {@inheritDoc}
    /// Use [EmbedConfig#errorSource(EmbedDataSource)] to replace the default embed of this error message. Alternatively,
    /// pass your own [ErrorMessageFactory] implementation to [JDACBuilder#errorMessageFactory(ErrorMessageFactory)].
    @Override
    public MessageCreateData getConstraintFailedMessage(ErrorContext context, String message) {
        return build(Container.of(
                        TextDisplay.of("jdac$constraint-failed")),
                Color.ORANGE,
                context.event().getUserLocale(),
                entry("message", message)
        );
    }

    /// {@inheritDoc}
    /// Use [EmbedConfig#errorSource(EmbedDataSource)] to replace the default embed of this error message. Alternatively,
    /// pass your own [ErrorMessageFactory] implementation to [JDACBuilder#errorMessageFactory(ErrorMessageFactory)].
    @Override
    public MessageCreateData getCommandExecutionFailedMessage(ErrorContext context, Throwable exception) {
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
                entry("timestamp", dateFormat.format(System.currentTimeMillis())),
                entry("exception", exception)
        );
    }

    /// {@inheritDoc}
    /// Use [EmbedConfig#errorSource(EmbedDataSource)] to replace the default embed of this error message. Alternatively,
    /// pass your own [ErrorMessageFactory] implementation to [JDACBuilder#errorMessageFactory(ErrorMessageFactory)].
    @Override
    public MessageCreateData getTimedOutComponentMessage(GenericInteractionCreateEvent event) {
        return build(Container.of(TextDisplay.of("jdac$unknown-interaction")), Color.RED, event.getUserLocale());
    }

    private MessageCreateData build(Container container, Color color, DiscordLocale locale, Entry... placeholders) {
        return new MessageCreateBuilder().useComponentsV2().setComponents(resolver.resolve(
                container.withAccentColor(color),
                locale.toLocale(),
                Entry.toMap(placeholders))
        ).build();
    }
}
