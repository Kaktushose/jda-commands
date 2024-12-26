package com.github.kaktushose.jda.commands.definitions.interactions.impl.command;

import com.github.kaktushose.jda.commands.annotations.interactions.Cooldown;
import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.interactions.MethodBuildContext;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.AutoCompleteDefinition;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
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

public final class SlashCommandDefinition extends CommandDefinition {

    private final String description;
    private final SequencedCollection<ParameterDefinition> commandParameters;
    private final SlashCommandDefinition.CooldownDefinition cooldown;
    private final boolean isAutoComplete;

    public SlashCommandDefinition(@NotNull ClassDescription clazz,
                                  @NotNull MethodDescription method,
                                  @NotNull Collection<String> permissions,
                                  @NotNull String name,
                                  @NotNull SlashCommand.CommandScope scope,
                                  boolean isGuildOnly,
                                  boolean isNSFW,
                                  @NotNull Set<Permission> enabledPermissions,
                                  @NotNull LocalizationFunction localizationFunction,
                                  @NotNull String description,
                                  @NotNull SequencedCollection<ParameterDefinition> commandParameters,
                                  @NotNull CooldownDefinition cooldown,
                                  boolean isAutoComplete) {
        super(clazz, method, permissions, name, Type.SLASH, scope, isGuildOnly, isNSFW, enabledPermissions, localizationFunction);
        this.description = description;
        this.commandParameters = commandParameters;
        this.cooldown = cooldown;
        this.isAutoComplete = isAutoComplete;
    }

    public static Optional<Definition> build(MethodBuildContext context) {
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

        boolean autoComplete = context.autoCompleteDefinitions().stream()
                .map(AutoCompleteDefinition::commands)
                .flatMap(Collection::stream)
                .anyMatch(name::startsWith);

        // build parameter definitions
        List<ParameterDefinition> parameters = method.parameters().stream()
                .filter(it -> !(CommandEvent.class.isAssignableFrom(it.type())))
                .map(parameter -> ParameterDefinition.build(parameter, autoComplete, context.validatorRegistry()))
                .toList();

        Set<Permission> enabledFor = Set.of(command.enabledFor());
        if (enabledFor.size() == 1 && enabledFor.contains(Permission.UNKNOWN)) {
            enabledFor = Set.of();
        }

        List<Class<?>> signature = new ArrayList<>();
        signature.add(CommandEvent.class);
        parameters.forEach(it -> signature.add(it.type()));
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
                parameters,
                cooldownDefinition,
                autoComplete
        ));
    }

    @NotNull
    @Override
    public SlashCommandData toJDAEntity() {
        SlashCommandData command = Commands.slash(
                name,
                description.replaceAll("N/A", "no description")
        );
        command.setGuildOnly(isGuildOnly)
                .setNSFW(isNSFW)
                .setLocalizationFunction(localizationFunction)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(enabledPermissions));
        commandParameters.forEach(parameter -> {
            if (CommandEvent.class.isAssignableFrom(parameter.type())) {
                return;
            }
            command.addOptions(parameter.toJDAEntity());
        });
        return command;
    }

    public SubcommandData toSubCommandData(String label) {
        SubcommandData command = new SubcommandData(
                label,
                description.replaceAll("N/A", "no description")

        );
        commandParameters.forEach(parameter -> {
            command.addOptions(parameter.toJDAEntity());
        });
        return command;
    }

    @NotNull
    @Override
    public String displayName() {
        return "/%s".formatted(name);
    }

    public CooldownDefinition cooldown() {
        return cooldown;
    }

    public boolean autoComplete() {
        return isAutoComplete;
    }

    public String description() {
        return description;
    }

    public SequencedCollection<ParameterDefinition> commandParameters() {
        return commandParameters;
    }

    public record CooldownDefinition(long delay, TimeUnit timeUnit) implements Definition {

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
