package com.github.kaktushose.jda.commands.definitions.interactions.command;

import com.github.kaktushose.jda.commands.annotations.interactions.CommandScope;
import com.github.kaktushose.jda.commands.annotations.interactions.Cooldown;
import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.description.ParameterDescription;
import com.github.kaktushose.jda.commands.definitions.interactions.AutoCompleteDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.AutoCompleteDefinition.AutoCompleteRule;
import com.github.kaktushose.jda.commands.definitions.interactions.MethodBuildContext;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/// Representation of a slash command.
///
/// @param classDescription     the [ClassDescription] of the declaring class of the [#methodDescription()]
/// @param methodDescription    the [MethodDescription] of the method this definition is bound to
/// @param permissions          a [Collection] of permissions for this command
/// @param name                 the name of the command
/// @param scope                the [CommandScope] of this command
/// @param guildOnly            whether this command can only be executed in guilds
/// @param nsfw                 whether this command is nsfw
/// @param enabledPermissions   a possibly-empty [Set] of [Permission]s this command will be enabled for
/// @param localizationFunction the [LocalizationFunction] to use for this command
/// @param description          the command description
/// @param commandOptions       a [SequencedCollection] of [OptionDataDefinition]s
/// @param cooldown             the corresponding [CooldownDefinition]
public record SlashCommandDefinition(
        @NotNull ClassDescription classDescription,
        @NotNull MethodDescription methodDescription,
        @NotNull Collection<String> permissions,
        @NotNull String name,
        @NotNull CommandScope scope,
        boolean guildOnly,
        boolean nsfw,
        @NotNull Set<Permission> enabledPermissions,
        @NotNull LocalizationFunction localizationFunction,
        @NotNull String description,
        @NotNull SequencedCollection<OptionDataDefinition> commandOptions,
        @NotNull CooldownDefinition cooldown
) implements CommandDefinition {

    /// Builds a new [SlashCommandDefinition] from the given [MethodBuildContext].
    ///
    /// @return an [Optional] holding the [SlashCommandDefinition]
    @NotNull
    public static Optional<SlashCommandDefinition> build(MethodBuildContext context) {
        var method = context.method();
        var interaction = context.interaction();
        var command = method.annotation(SlashCommand.class).orElseThrow();

        String name = String.join(" ", interaction.value(), command.value())
                .replaceAll(" +", " ")
                .trim();

        if (name.isEmpty()) {
            Checks.notBlank(name, "Command name");
            return Optional.empty();
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

        Set<Permission> enabledFor = Set.of(command.enabledFor());
        if (enabledFor.size() == 1 && enabledFor.contains(Permission.UNKNOWN)) {
            enabledFor = Set.of();
        }

        List<Class<?>> signature = new ArrayList<>();
        signature.add(CommandEvent.class);
        commandOptions.forEach(it -> signature.add(it.type()));
        if (Helpers.checkSignature(method, signature)) {
            return Optional.empty();
        }

        CooldownDefinition cooldownDefinition = CooldownDefinition.build(method.annotation(Cooldown.class).orElse(null));
        if (cooldownDefinition.delay() == 0 && context.cooldownDefinition() != null) {
            cooldownDefinition = context.cooldownDefinition();
        }

        return Optional.of(new SlashCommandDefinition(
                context.clazz(),
                method,
                Helpers.permissions(context),
                name,
                command.scope(),
                command.isGuildOnly(),
                command.isNSFW(),
                enabledFor,
                context.localizationFunction(),
                command.desc(),
                commandOptions,
                cooldownDefinition
        ));
    }

    @Nullable
    private static AutoCompleteDefinition findAutoComplete(List<AutoCompleteDefinition> autoCompletes, ParameterDescription parameter, String command) {
        var possibleAutoCompletes = autoCompletes.stream()
                .filter(definition -> definition.rules().stream()
                        .flatMap(rule -> rule.options().stream())
                        .anyMatch(it -> it.equals(parameter.name()))
                ).toList();
        if (possibleAutoCompletes.size() > 1) {
            log.error("""
                            Found multiple auto complete handler for parameter named "{}" of slash command "/{}":
                                 -> {}
                            Every command option can only have one auto complete handler. Please exclude the unwanted ones to enable auto complete for this command option.""",
                    parameter.name(),
                    command,
                    possibleAutoCompletes.stream().map(AutoCompleteDefinition::displayName).collect(Collectors.joining("\n     -> "))
            );
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
    @NotNull
    @Override
    public SlashCommandData toJDAEntity() {
        SlashCommandData command = Commands.slash(
                name,
                description.replaceAll("N/A", "no description")
        );
        command.setGuildOnly(guildOnly)
                .setNSFW(nsfw)
                .setLocalizationFunction(localizationFunction)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(enabledPermissions));
        commandOptions.forEach(parameter -> {
            if (CommandEvent.class.isAssignableFrom(parameter.type())) {
                return;
            }
            command.addOptions(parameter.toJDAEntity());
        });
        return command;
    }

    /// Transforms this definition into [SubcommandData].
    ///
    /// @return the [SubcommandData]
    public SubcommandData toSubCommandData(String label) {
        SubcommandData command = new SubcommandData(
                label,
                description.replaceAll("N/A", "no description")

        );
        commandOptions.forEach(parameter -> command.addOptions(parameter.toJDAEntity()));
        return command;
    }

    @NotNull
    @Override
    public String displayName() {
        return "/%s".formatted(name);
    }

    @NotNull
    @Override
    public Command.Type commandType() {
        return Command.Type.SLASH;
    }

    /// Representation of a cooldown definition defined by [Cooldown].
    ///
    /// @param delay    the delay of the cooldown
    /// @param timeUnit the [TimeUnit] of the specified delay
    public record CooldownDefinition(long delay, TimeUnit timeUnit) implements Definition {

        /// Builds a new [CooldownDefinition] from the given [Cooldown] annotation.
        @NotNull
        public static CooldownDefinition build(@Nullable Cooldown cooldown) {
            if (cooldown == null) {
                return new CooldownDefinition(0, TimeUnit.MILLISECONDS);
            }
            return new CooldownDefinition(cooldown.value(), cooldown.timeUnit());
        }

        @NotNull
        @Override
        public String displayName() {
            return "Cooldown of %d %s".formatted(delay, timeUnit.name());
        }
    }
}
