package io.github.kaktushose.jdac.definitions.interactions.command;

import io.github.kaktushose.jdac.annotations.interactions.Command;
import io.github.kaktushose.jdac.definitions.description.ClassDescription;
import io.github.kaktushose.jdac.definitions.description.MethodDescription;
import io.github.kaktushose.jdac.definitions.description.ParameterDescription;
import io.github.kaktushose.jdac.definitions.interactions.AutoCompleteDefinition;
import io.github.kaktushose.jdac.definitions.interactions.AutoCompleteDefinition.AutoCompleteRule;
import io.github.kaktushose.jdac.definitions.interactions.MethodBuildContext;
import io.github.kaktushose.jdac.dispatching.events.interactions.CommandEvent;
import io.github.kaktushose.jdac.exceptions.InvalidDeclarationException;
import io.github.kaktushose.jdac.exceptions.internal.JDACException;
import io.github.kaktushose.jdac.internal.Helpers;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

/// Representation of a slash command.
///
/// @param classDescription     the [ClassDescription] of the declaring class of the [#methodDescription()]
/// @param methodDescription    the [MethodDescription] of the method this definition is bound to
/// @param permissions          a [Collection] of permissions for this command
/// @param name                 the name of the command
/// @param commandConfig        the [CommandConfig] to use
/// @param localizationFunction the [LocalizationFunction] to use for this command
/// @param description          the command description
/// @param commandOptions       a [SequencedCollection] of [OptionDataDefinition]s
public record SlashCommandDefinition(
        ClassDescription classDescription,
        MethodDescription methodDescription,
        Collection<String> permissions,
        String name,
        CommandConfig commandConfig,
        LocalizationFunction localizationFunction,
        String description,
        SequencedCollection<OptionDataDefinition> commandOptions
) implements CommandDefinition {

    /// Builds a new [SlashCommandDefinition] from the given [MethodBuildContext].
    ///
    /// @return an [Optional] holding the [SlashCommandDefinition]
    public static SlashCommandDefinition build(MethodBuildContext context) {
        var method = context.method();
        var interaction = context.interaction();
        var command = method.annotation(Command.class).orElseThrow();
        String description = command.desc().equals("N/A")
                ? context.i18n().localize(Locale.ENGLISH, "jdac$no-description")
                : command.desc();

        String name = String.join(" ", interaction.value(), command.value())
                .replaceAll(" +", " ")
                .trim();

        // we have to enforce this here to not break the CommandTree
        if (name.split(" ").length > 3) {
            throw new InvalidDeclarationException(
                    "command-name-length",
                    entry("name", name),
                    entry("method", "%s.%s".formatted(context.clazz().name(), method.name()))
            );
        }

        var autoCompletes = context.autoCompleteDefinitions().stream()
                .filter(definition -> definition.rules().stream()
                        .map(AutoCompleteRule::command)
                        .anyMatch(it -> name.startsWith(it) || it.equals(method.name()))
                ).toList();

        // build option data definitions
        List<OptionDataDefinition> commandOptions = method.parameters().stream()
                .filter(it -> !(CommandEvent.class.isAssignableFrom(it.type())))
                .map(parameter ->
                        OptionDataDefinition.build(parameter, findAutoComplete(autoCompletes, parameter, name), context.i18n(), context.validators())
                )
                .toList();

        List<Class<?>> signature = new ArrayList<>();
        signature.add(CommandEvent.class);
        commandOptions.forEach(it -> signature.add(it.declaredType()));
        Helpers.checkSignature(method, signature);

        return new SlashCommandDefinition(
                context.clazz(),
                method,
                Helpers.permissions(context),
                name,
                Helpers.commandConfig(context),
                context.localizationFunction(),
                description,
                commandOptions
        );
    }

    @Nullable
    private static AutoCompleteDefinition findAutoComplete(List<AutoCompleteDefinition> autoCompletes, ParameterDescription parameter, String command) {
        var possibleAutoCompletes = autoCompletes.stream()
                .filter(definition -> definition.rules().stream()
                        .flatMap(rule -> rule.options().stream())
                        .anyMatch(it -> it.equals(parameter.name()))
                ).toList();
        if (possibleAutoCompletes.size() > 1) {
            log.error(JDACException.errorMessage("multiple-autocomplete",
                    entry("name", parameter.name()),
                    entry("command", command),
                    entry("possibleAutoCompletes", possibleAutoCompletes.stream().map(AutoCompleteDefinition::displayName).collect(Collectors.joining("\n     -> "))
                    )));
            return null;
        }
        if (possibleAutoCompletes.isEmpty()) {
            return autoCompletes.stream()
                    .filter(definition -> definition.rules().stream()
                            .anyMatch(rule -> rule.options().isEmpty())
                    ).findFirst().orElse(null);
        }
        return possibleAutoCompletes.getFirst();
    }

    /// Transforms this definition into [SlashCommandData].
    ///
    /// @return the [SlashCommandData]
    @Override
    public SlashCommandData toJDAEntity() {
        try {
            SlashCommandData command = Commands.slash(
                    name,
                    description
            );

            command.setIntegrationTypes(commandConfig.integration())
                    .setContexts(commandConfig.context())
                    .setNSFW(commandConfig.isNSFW())
                    .setLocalizationFunction(localizationFunction)
                    .setDefaultPermissions(DefaultMemberPermissions.enabledFor(commandConfig.enabledPermissions()))
                    .addOptions(commandOptions.stream()
                            .filter(it -> !CommandEvent.class.isAssignableFrom(it.declaredType()))
                            .map(OptionDataDefinition::toJDAEntity)
                            .toList()
                    );
            return command;
        } catch (IllegalArgumentException e) {
            throw Helpers.jdaException(e, this);
        }
    }

    /// Transforms this definition into [SubcommandData].
    ///
    /// @return the [SubcommandData]
    public SubcommandData toSubcommandData(String name) {
        try {
            return new SubcommandData(name, description)
                    .addOptions(commandOptions.stream()
                            .filter(it -> !CommandEvent.class.isAssignableFrom(it.declaredType()))
                            .map(OptionDataDefinition::toJDAEntity)
                            .toList()
                    );
        } catch (IllegalArgumentException e) {
            throw Helpers.jdaException(e, this);
        }
    }

    @Override
    public String displayName() {
        return "/%s".formatted(name);
    }

    @Override
    public net.dv8tion.jda.api.interactions.commands.Command.Type commandType() {
        return net.dv8tion.jda.api.interactions.commands.Command.Type.SLASH;
    }
}
