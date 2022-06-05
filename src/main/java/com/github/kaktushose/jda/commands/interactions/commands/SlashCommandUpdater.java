package com.github.kaktushose.jda.commands.interactions.commands;

import com.github.kaktushose.jda.commands.JDAContext;
import com.github.kaktushose.jda.commands.data.slash.CommandTree;
import com.github.kaktushose.jda.commands.dispatching.interactions.HelpAutoCompleteListener;
import com.github.kaktushose.jda.commands.reflect.CommandDefinition;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class that sends the {@link SlashCommandData} to Discord. Uses a {@link CommandTree} to properly transpile all
 * {@link CommandDefinition CommandDefinitions} to {@link SlashCommandData}. Also registers the
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
    private final SlashConfiguration configuration;
    private final HelpAutoCompleteListener autoCompleteListener;

    /**
     * Constructs a new SlashCommandUpdater.
     *
     * @param jdaContext    the corresponding {@link JDAContext}
     * @param configuration the corresponding {@link SlashConfiguration}
     */
    public SlashCommandUpdater(JDAContext jdaContext, SlashConfiguration configuration) {
        this.jdaContext = jdaContext;
        this.configuration = configuration;
        autoCompleteListener = new HelpAutoCompleteListener();
        jdaContext.performTask(jda -> jda.addEventListener(autoCompleteListener));
        jda = jdaContext.getJda();
    }

    /**
     * Sends the {@link SlashCommandData} to Discord. Depending on the {@link SlashConfiguration} this will register
     * guild or global commands.
     *
     * @param commands a {@link Collection} of {@link SlashCommandData} to update
     */
    public void update(Collection<CommandDefinition> commands) {
        log.debug("Updating slash commands with {} scope...", configuration.isGlobal() ? "global" : "guild");
        if (configuration.getPolicy() == CommandRegistrationPolicy.TEXT) {
            log.info("CommandRegistrationPolicy is set to TEXT. Unregistering all slash commands!");
            push(Collections.emptyList());
            log.debug("Done!");
            return;
        }
        CommandTree tree = new CommandTree(commands);
        log.debug("Generated command tree:\n" + tree);
        Collection<SlashCommandData> commandData = tree.getCommands();
        Collection<String> labels = tree.getNames();
        log.debug("Using commands: " + labels);
        addHelpCommands(commandData, labels);
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
        if (configuration.isGlobal()) {
            jda.updateCommands().addCommands(commands).queue();
        } else {
            configuration.getGuildIds()
                    .stream().map(id -> Optional.ofNullable(jdaContext.getGuildCache().getElementById(id)))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(guild -> guild.updateCommands().addCommands(commands).queue());
        }
    }

    /**
     * Shutdowns the {@link HelpAutoCompleteListener}. This will <b>not</b> unregister any slash commands.
     */
    public void shutdown() {
        jdaContext.performTask(jda -> jda.removeEventListener(autoCompleteListener));
    }

}
