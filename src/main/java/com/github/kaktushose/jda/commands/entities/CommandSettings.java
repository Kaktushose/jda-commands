package com.github.kaktushose.jda.commands.entities;

import com.github.kaktushose.jda.commands.rewrite.exceptions.CommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class provides a very basic level of configuration.
 *
 * <p>All settings declared through this class can also be declared on a guild specific level. Therefore use the
 * {@link JDACommands#getGuildSettings()} method. If no guild settings are present, the default settings will be
 * used instead.
 *
 * @author Kaktushose
 * @version 1.1.1
 * @see JDACommands
 * @since 1.0.0
 */
public class CommandSettings {

    private final static Logger log = LoggerFactory.getLogger(CommandSettings.class);
    private final Set<Long> mutedChannels;
    private final Set<Long> mutedUsers;
    private final Set<String> helpLabels;
    private final Map<String, Set<Long>> permissionHolders;
    private final Map<String, Long> permissionRoles;
    private final Set<String> prefixAliases;
    private boolean botMentionPrefix;
    private String prefix;
    private boolean ignoreBots;
    private boolean ignoreLabelCase;
    private boolean embedAtNotFound;

    /**
     * Constructs a new CommandSettings object with default values.
     */
    public CommandSettings() {
        this("!", true, true, true, true);
    }

    /**
     * Constructs a new CommandSettings object.
     *
     * @param prefix           the default prefix the framework will listen to
     * @param ignoreBots       whether the framework should ignore messages from Discord Bots or not
     * @param ignoreLabelCase  whether the command mapper should be case sensitive or not
     * @param botMentionPrefix whether to allow a bot mention to be a valid prefix or not
     * @param embedAtNotFound  whether to send an embed at a non-existing command or not
     */
    public CommandSettings(@Nullable String prefix, boolean ignoreBots, boolean ignoreLabelCase, boolean botMentionPrefix, boolean embedAtNotFound) {
        this.prefix = validatePrefix(prefix);
        this.prefixAliases = new HashSet<>();
        this.ignoreBots = ignoreBots;
        this.ignoreLabelCase = ignoreLabelCase;
        this.botMentionPrefix = botMentionPrefix;
        this.embedAtNotFound = embedAtNotFound;
        mutedChannels = ConcurrentHashMap.newKeySet();
        mutedUsers = ConcurrentHashMap.newKeySet();
        permissionHolders = new ConcurrentHashMap<>();
        permissionRoles = new ConcurrentHashMap<>();
        helpLabels = ConcurrentHashMap.newKeySet();
        helpLabels.add("help");
    }

    /**
     * Get the default prefix.
     *
     * @return the default prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the default prefix. If the prefix is {@code null} the fallback prefix <em>!</em> will be used instead.
     *
     * @param prefix the default prefix to set
     * @return the current instance to use fluent interface
     */
    public CommandSettings setPrefix(@Nullable String prefix) {
        this.prefix = validatePrefix(prefix);
        return this;
    }

    /**
     * Get a Set of prefix aliases. This Set is mutable and thus can be modified in
     * order to add or remove a prefix alias. Returns an empty Set if no prefix aliases have been added yet.
     *
     * @return a possibly-empty mutable Set of prefix aliases
     */
    public Set<String> getPrefixAliases() {
        return prefixAliases;
    }

    /**
     * Get a Set of the ids of all users with the given permission. This Set is mutable and thus can be modified in
     * order to add or remove users from the given permission level. Returns an empty Set if no users have the
     * given permission level.
     *
     * @param permission the permission to get the users for
     * @return a mutable Set containing the ids of all users with the given permission
     * @see com.github.kaktushose.jda.commands.rewrite.annotations.Permission
     */
    public Set<Long> getPermissionHolders(@Nonnull String permission) {
        permissionHolders.putIfAbsent(permission, ConcurrentHashMap.newKeySet());
        return permissionHolders.get(permission);
    }

    /**
     * Get the role for the given permission.
     *
     * @param permission the permission to get the role for
     * @return the id of the role
     * @see com.github.kaktushose.jda.commands.rewrite.annotations.Permission
     */
    public Long getPermissionRole(@Nonnull String permission) {
        return permissionRoles.get(permission);
    }

    /**
     * Get a Map of all permissions and their holders. Key: the String representing a permission. Value: a
     * Set of the ids of all users with the permission. This Map is mutable and thus can be modified in
     * order to add or remove permissions and their holders.
     *
     * @return a mutable Map containing all permissions and the ids of all users with the permissions
     */
    public Map<String, Set<Long>> getAllPermissionHolders() {
        return permissionHolders;
    }

    /**
     * Get a Map of all permissions and their roles. Key: the String representing a permission. Value: a
     * The id of the role
     *
     * @return a mutable Map containing all permissions and the ids of the roles with the permissions
     */
    public Map<String, Long> getAllPermissionRoles() {
        return permissionRoles;
    }

    /**
     * Get a Set of the ids of all muted TextChannels. This Set is mutable and thus can be modified in order to mute or unmute a
     * TextChannel.
     *
     * @return a mutable Set containing the ids of all muted TextChannels.
     */
    public Set<Long> getMutedChannels() {
        return mutedChannels;
    }

    /**
     * Get a Set of the ids of all muted Users. This Set is mutable and thus can be modified in order to mute or unmute a
     * User.
     *
     * @return a mutable Set containing the ids of all muted Users.
     */
    public Set<Long> getMutedUsers() {
        return mutedUsers;
    }

    /**
     * Get a Set of all specified help labels. This Set is mutable and thus can be modified in order to add or remove a
     * help label. The default value is <em>help</em>.
     *
     * @return a mutable Set containing all help labels.
     */
    public Set<String> getHelpLabels() {
        return helpLabels;
    }

    /**
     * Whether bots should be ignored by this framework or not. If set true, bot accounts can also trigger commands.
     *
     * @return {@code true} if bot accounts
     */
    public boolean isIgnoreBots() {
        return ignoreBots;
    }

    /**
     * Set if bots should be ignored by this framework or not. If set false, bot accounts can also trigger commands.
     *
     * @param ignoreBots true if bots should not invoke commands
     * @return the current instance to use fluent interface
     */
    public CommandSettings setIgnoreBots(boolean ignoreBots) {
        this.ignoreBots = ignoreBots;
        return this;
    }

    /**
     * Whether the case of a label should be ignored or not. For instance, if set true, input <em>FOO</em> will match
     * the label <em>foo</em>
     *
     * @return {@code true} if the label case is ignored
     */
    public boolean isIgnoreLabelCase() {
        return ignoreLabelCase;
    }

    /**
     * Set the case of a label should be ignored or true. For instance, if set true, input <em>FOO</em> will match
     * the label <em>foo</em>
     *
     * @param ignoreLabelCase true if the label case should be ignored
     * @return the current instance to use fluent interface
     */
    public CommandSettings setIgnoreLabelCase(boolean ignoreLabelCase) {
        this.ignoreLabelCase = ignoreLabelCase;
        return this;
    }

    /**
     * Whether a mention of the bot account should be a valid prefix or not.
     *
     * @return {@code true} if a bot mention is a valid prefix
     */
    public boolean isBotMentionPrefix() {
        return botMentionPrefix;
    }

    /**
     * Set if a mention of the bot account should be a valid prefix or not.
     *
     * @param botMentionPrefix {@code true} if a bot mention is a valid prefix
     * @return the current instance to use fluent interface
     */
    public CommandSettings setBotMentionPrefix(boolean botMentionPrefix) {
        this.botMentionPrefix = botMentionPrefix;
        return this;
    }

    private String validatePrefix(String prefix) {
        if (prefix == null) {
            log.error("Switching to default value '!'", new CommandException("Prefix must not be null!"));
            return "!";
        }
        return prefix;
    }

    /**
     * Whether to send an embed at a non-existing command or not.
     *
     * @return {@code true} if it should send an embed at a non-existing command
     */
    public boolean isEmbedAtNotFound() {
        return embedAtNotFound;
    }

    /**
     * Set if it should send an embed at a non-existing command or not.
     *
     * @param embedAtNotFound {@code true} if it should send an embed at a non-existing command
     * @return the current instance to use fluent interface
     */
    public CommandSettings setEmbedAtNotFound(boolean embedAtNotFound) {
        this.embedAtNotFound = embedAtNotFound;
        return this;
    }
}
