package com.github.kaktushose.jda.commands;

import com.github.kaktushose.jda.commands.slash.CommandRegistrationPolicy;
import com.github.kaktushose.jda.commands.dispatching.slash.SlashConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Builder class for using slash commands.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see JDACommands
 * @since 2.3.0
 */
public class JDACommandsSlashBuilder {

    private final List<Long> guildIds;
    private final Object jda;
    private final boolean isShardManager;
    private final Class<?> clazz;
    private final String[] packages;
    private CommandRegistrationPolicy policy;

    JDACommandsSlashBuilder(Object jda, boolean isShardManager, Class<?> clazz, String... packages) {
        this.jda = jda;
        this.isShardManager = isShardManager;
        this.clazz = clazz;
        this.packages = packages;
        this.guildIds = new ArrayList<>();
    }

    /**
     * Adds guilds where slash commands will be registered.
     *
     * @param ids the guild ids to add
     * @return the current instance for fluent interface
     */
    public JDACommandsSlashBuilder guilds(long... ids) {
        for (long id : ids) {
            guildIds.add(id);
        }
        return this;
    }

    /**
     * Adds guilds where slash commands will be registered.
     *
     * @param ids a {@link Collection} of guild ids to add
     * @return the current instance for fluent interface
     */
    public JDACommandsSlashBuilder guilds(@NotNull Collection<Long> ids) {
        guildIds.addAll(ids);
        return this;
    }

    /**
     * Defines the {@link CommandRegistrationPolicy} to use for registering commands.
     * The default value is {@link CommandRegistrationPolicy#SLASH}
     *
     * @param policy the {@link CommandRegistrationPolicy} to use
     * @return the current instance for fluent interface
     */
    public JDACommandsSlashBuilder registrationPolicy(@NotNull CommandRegistrationPolicy policy) {
        this.policy = policy;
        return this;
    }

    /**
     * Starts JDACommands with the predefined values. This will register slash commands as global commands, ignoring the
     * guilds passed to {@link #guilds(long...)}.
     *
     * @return {@link JDACommands}
     */
    @NotNull
    public JDACommands startGlobal() {
        return new JDACommands(jda, isShardManager, clazz, new SlashConfiguration(guildIds, true, policy), packages);
    }

    /**
     * Starts JDACommands with the predefined values. This will register slash commands as guild commands for the
     * guilds defined to {@link #guilds(long...)}.
     *
     * @return {@link JDACommands}
     */
    @NotNull
    public JDACommands startGuild() {
        return new JDACommands(jda, isShardManager, clazz, new SlashConfiguration(guildIds, false, policy), packages);
    }

}
