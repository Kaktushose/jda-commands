package com.github.kaktushose.jda.commands.definitions.interactions.command;

import com.github.kaktushose.jda.commands.annotations.interactions.Command;
import com.github.kaktushose.jda.commands.annotations.interactions.Cooldown;
import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.description.ParameterDescription;
import com.github.kaktushose.jda.commands.definitions.interactions.AutoCompleteDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.AutoCompleteDefinition.AutoCompleteRule;
import com.github.kaktushose.jda.commands.definitions.interactions.MethodBuildContext;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.exceptions.InvalidDeclarationException;
import com.github.kaktushose.jda.commands.exceptions.internal.JDACException;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.github.kaktushose.jda.commands.i18n.I18n.entry;

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
/// @param cooldown             the corresponding [CooldownDefinition]
public record SlashCommandDefinition(
        ClassDescription classDescription,
        MethodDescription methodDescription,
        Collection<String> permissions,
        String name,
        CommandConfig commandConfig,
        LocalizationFunction localizationFunction,
        String description,
        SequencedCollection<OptionDataDefinition> commandOptions,
        CooldownDefinition cooldown
) implements CommandDefinition {

    /// Builds a new [SlashCommandDefinition] from the given [MethodBuildContext].
    ///
    /// @return an [Optional] holding the [SlashCommandDefinition]
    
    public static SlashCommandDefinition build(MethodBuildContext context) {
        var method = context.method();
        var interaction = context.interaction();
        var command = method.annotation(Command.class).orElseThrow();

        String name = String.join(" ", interaction.value(), command.value())
                .replaceAll(" +", " ")
                .trim();

        if (name.isBlank()) {
            throw new InvalidDeclarationException("blank-name");
        }

        String[] split = name.split(" ");
        if (split.length > 3) {
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
                        OptionDataDefinition.build(parameter, findAutoComplete(autoCompletes, parameter, name), context.validators())
                )
                .toList();

        List<Class<?>> signature = new ArrayList<>();
        signature.add(CommandEvent.class);
        commandOptions.forEach(it -> signature.add(it.declaredType()));
        Helpers.checkSignature(method, signature);

        CooldownDefinition cooldownDefinition = CooldownDefinition.build(method.annotation(Cooldown.class).orElse(null));
        if (cooldownDefinition.delay() == 0 && context.cooldownDefinition() != null) {
            cooldownDefinition = context.cooldownDefinition();
        }

        return new SlashCommandDefinition(
                context.clazz(),
                method,
                Helpers.permissions(context),
                name,
                Helpers.commandConfig(context),
                context.localizationFunction(),
                command.desc(),
                commandOptions,
                cooldownDefinition
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
        SlashCommandData command = Commands.slash(
                name,
                description.replaceAll("N/A", "no description")
        );
        command.setIntegrationTypes(commandConfig.integration())
                .setContexts(commandConfig.context())
                .setNSFW(commandConfig.isNSFW())
                .setLocalizationFunction(localizationFunction)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(commandConfig.enabledPermissions()));
        commandOptions.forEach(parameter -> {
            if (CommandEvent.class.isAssignableFrom(parameter.declaredType())) {
                return;
            }
            command.addOptions(parameter.toJDAEntity());
        });
        return command;
    }

    /// Transforms this definition into [SubcommandData].
    ///
    /// @return the [SubcommandData]
    public SubcommandData toSubcommandData(String name) {
        SubcommandData command = new SubcommandData(
                name,
                description.replaceAll("N/A", "no description")

        );
        commandOptions.forEach(parameter -> command.addOptions(parameter.toJDAEntity()));
        return command;
    }

    
    @Override
    public String displayName() {
        return "/%s".formatted(name);
    }

    
    @Override
    public net.dv8tion.jda.api.interactions.commands.Command.Type commandType() {
        return net.dv8tion.jda.api.interactions.commands.Command.Type.SLASH;
    }

    /// Representation of a cooldown definition defined by [Cooldown].
    ///
    /// @param delay    the delay of the cooldown
    /// @param timeUnit the [TimeUnit] of the specified delay
    public record CooldownDefinition(long delay, TimeUnit timeUnit) implements Definition {

        /// Builds a new [CooldownDefinition] from the given [Cooldown] annotation.
        
        public static CooldownDefinition build(@Nullable Cooldown cooldown) {
            if (cooldown == null) {
                return new CooldownDefinition(0, TimeUnit.MILLISECONDS);
            }
            return new CooldownDefinition(cooldown.value(), cooldown.timeUnit());
        }

        
        @Override
        public String displayName() {
            return "Cooldown of %d %s".formatted(delay, timeUnit.name());
        }
    }
}
