package com.github.kaktushose.jda.commands.interactions.commands;

import com.github.kaktushose.jda.commands.JDAContext;
import com.github.kaktushose.jda.commands.data.slash.CommandTree;
import com.github.kaktushose.jda.commands.reflect.interactions.CommandDefinition;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Class that sends the {@link SlashCommandData} to Discord. Uses a {@link CommandTree} to properly transpile all
 * {@link CommandDefinition CommandDefinitions} to {@link SlashCommandData}.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see CommandTree
 * @since 2.3.0
 */
public class SlashCommandUpdater {

    private static final Logger log = LoggerFactory.getLogger(SlashCommandUpdater.class);
    private final JDAContext jdaContext;
    private final JDA jda;

    /**
     * Constructs a new SlashCommandUpdater.
     *
     * @param jdaContext the corresponding {@link JDAContext}
     */
    public SlashCommandUpdater(JDAContext jdaContext) {
        this.jdaContext = jdaContext;
        jda = jdaContext.getJda();
    }

    /**
     * Sends the {@link SlashCommandData} to Discord.
     *
     * @param commands a {@link Collection} of {@link SlashCommandData} to update
     */
    public void update(Collection<CommandDefinition> commands) {
        log.debug("Updating slash commands...");
        CommandTree tree = new CommandTree(commands);
        log.debug("Generated command tree:\n" + tree);
        Collection<String> labels = tree.getNames();
        log.debug("Using commands: " + labels);
        Collection<SlashCommandData> commandData = tree.getCommands();
        push(commandData);
        log.debug("Done!");
    }

    private void push(Collection<SlashCommandData> commands) {
        // TODO add support for global and guild commands
        jda.getGuilds().forEach(guild -> guild.updateCommands().addCommands(commands).queue());
    }

}
