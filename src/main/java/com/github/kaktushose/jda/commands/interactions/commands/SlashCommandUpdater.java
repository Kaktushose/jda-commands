package com.github.kaktushose.jda.commands.interactions.commands;

import com.github.kaktushose.jda.commands.JDAContext;
import com.github.kaktushose.jda.commands.data.slash.CommandTree;
import com.github.kaktushose.jda.commands.dispatching.interactions.HelpAutoCompleteListener;
import com.github.kaktushose.jda.commands.reflect.interactions.SlashCommandDefinition;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Class that sends the {@link SlashCommandData} to Discord. Uses a {@link CommandTree} to properly transpile all
 * {@link SlashCommandDefinition CommandDefinitions} to {@link SlashCommandData}. Also registers the
 * {@link HelpAutoCompleteListener}.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see HelpAutoCompleteListener
 * @see CommandTree
 * @since 2.3.0
 */
public class SlashCommandUpdater {

    private static final Logger log = LoggerFactory.getLogger(SlashCommandUpdater.class);
    private final JDAContext jdaContext;
    private final JDA jda;
    private final HelpAutoCompleteListener autoCompleteListener;

    /**
     * Constructs a new SlashCommandUpdater.
     *
     * @param jdaContext the corresponding {@link JDAContext}
     */
    public SlashCommandUpdater(JDAContext jdaContext) {
        this.jdaContext = jdaContext;
        autoCompleteListener = new HelpAutoCompleteListener();
        jdaContext.performTask(jda -> jda.addEventListener(autoCompleteListener));
        jda = jdaContext.getJda();
    }

    /**
     * Sends the {@link SlashCommandData} to Discord.
     *
     * @param commands a {@link Collection} of {@link SlashCommandData} to update
     */
    public void update(Collection<SlashCommandDefinition> commands) {
        log.debug("Updating slash commands...");
        CommandTree tree = new CommandTree(commands);
        log.debug("Generated command tree:\n" + tree);
        Collection<String> labels = tree.getNames();
        log.debug("Using commands: " + labels);
        Collection<SlashCommandData> commandData = tree.getCommands();
        // TODO check of help enabled flag
        if (true) {
            addHelpCommands(commandData, labels);
        }
        push(commandData);
        log.debug("Done!");
    }

    private void addHelpCommands(Collection<SlashCommandData> commandData, Collection<String> labels) {
        OptionData optionData = new OptionData(OptionType.STRING, "command", "the command you need help with");
        if (labels.size() > 25) {
            optionData.setAutoComplete(true);
        } else {
            optionData.addChoices(labels.stream().map(s -> new Command.Choice(s, s)).collect(Collectors.toList()));
        }
        commandData.add(Commands.slash("help", "Get specific help for commands").addOptions(optionData));

        autoCompleteListener.setLabels(labels);
    }

    private void push(Collection<SlashCommandData> commands) {
        // TODO add support for global and guild commands
        jda.getGuilds().forEach(guild -> guild.updateCommands().addCommands(commands).queue());
    }

    /**
     * Shutdowns the {@link HelpAutoCompleteListener}. This will <b>not</b> unregister any slash commands.
     */
    public void shutdown() {
        jdaContext.performTask(jda -> jda.removeEventListener(autoCompleteListener));
    }

}
