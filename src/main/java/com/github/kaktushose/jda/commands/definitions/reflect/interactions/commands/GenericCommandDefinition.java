package com.github.kaktushose.jda.commands.definitions.reflect.interactions.commands;

import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.EphemeralInteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.ReplyConfig;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Generic base class of different command types.
 *
 * @see SlashCommandDefinition
 * @see ContextCommandDefinition
 * @since 4.0.0
 */
public abstract sealed class GenericCommandDefinition
        extends EphemeralInteractionDefinition
        implements Comparable<GenericCommandDefinition>
        permits ContextCommandDefinition, SlashCommandDefinition {

    protected final String name;
    protected final boolean isGuildOnly;
    protected final boolean isNSFW;
    protected final Command.Type commandType;
    protected final Set<net.dv8tion.jda.api.Permission> enabledPermissions;
    protected final SlashCommand.CommandScope scope;
    protected final LocalizationFunction localizationFunction;

    protected GenericCommandDefinition(Method method,
                                       ReplyConfig replyConfig,
                                       String name,
                                       Set<String> permissions,
                                       boolean isGuildOnly,
                                       boolean isNSFW,
                                       Command.Type commandType,
                                       Set<Permission> enabledPermissions,
                                       SlashCommand.CommandScope scope,
                                       LocalizationFunction localizationFunction) {
        super(method, permissions, replyConfig);
        this.name = name;
        this.isGuildOnly = isGuildOnly;
        this.isNSFW = isNSFW;
        this.commandType = commandType;
        this.enabledPermissions = enabledPermissions;
        this.scope = scope;
        this.localizationFunction = localizationFunction;
    }

    /**
     * Transforms this command definition to {@link CommandData}.
     *
     * @return the {@link CommandData}
     */
    public abstract CommandData toCommandData();

    /**
     * Gets the command name.
     *
     * @return the command name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the command name.
     *
     * @return the command name
     */
    public String getDisplayName() {
        return String.format("/%s", name);
    }

    /**
     * Whether this command can be executed inside direct messages.
     *
     * @return {@code true} if this command can be executed inside direct messages
     */
    public boolean isGuildOnly() {
        return isGuildOnly;
    }

    /**
     * Whether this command can only be executed in NSFW channels.
     *
     * @return {@code true} if this command can only be executed in NSFW channels
     */
    public boolean isNSFW() {
        return isNSFW;
    }

    /**
     * Gets a set of Discord permission Strings this command will be enabled for by default.
     *
     * @return a set of Discord permission Strings this command will be enabled for by default
     */
    public Set<net.dv8tion.jda.api.Permission> getEnabledPermissions() {
        return enabledPermissions;
    }

    public Command.Type getCommandType() {
        return commandType;
    }

    public LocalizationFunction getLocalizationFunction() {
        return localizationFunction;
    }

    /**
     * Gets the {@link SlashCommand.CommandScope CommandScope} of this command.
     *
     * @return the {@link SlashCommand.CommandScope CommandScope} of this command
     */
    public SlashCommand.CommandScope getCommandScope() {
        return scope;
    }

    @Override
    public String toString() {
        return "GenericCommandDefinition{" +
                "id='" + definitionId + '\'' +
                ", method=" + method +
                ", name='" + name + '\'' +
                ", permissions=" + permissions +
                ", isGuildOnly=" + isGuildOnly +
                ", isNSFW=" + isNSFW +
                ", commandType=" + commandType +
                ", enabledPermissions=" + enabledPermissions +
                ", scope=" + scope +
                ", localizationFunction=" + localizationFunction +
                ", replyConfig=" + replyConfig +
                '}';
    }

    @Override
    public int compareTo(@NotNull GenericCommandDefinition command) {
        return name.compareTo(command.name);
    }

}
