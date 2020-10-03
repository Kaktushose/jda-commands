package com.github.kaktushose.jda.commands.internal;

import com.github.kaktushose.jda.commands.api.*;
import com.github.kaktushose.jda.commands.entities.CommandCallable;
import com.github.kaktushose.jda.commands.entities.CommandList;
import com.github.kaktushose.jda.commands.entities.CommandSettings;
import com.github.kaktushose.jda.commands.entities.JDACommands;
import com.github.kaktushose.jda.commands.exceptions.CommandException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class CommandDispatcher extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(CommandDispatcher.class);
    private static boolean isActive;
    private final CommandSettings settings;
    private final CommandList commands;
    private final CommandRegistry commandRegistry;
    private final EventParser eventParser;
    private final CommandMapper commandMapper;
    private final ArgumentParser argumentParser;
    private final EmbedFactory embedFactory;
    private final DependencyInjector dependencyInjector;
    private final HelpMessageSender helpMessageSender;
    private final Object jda;
    private final boolean isShardManager;
    private JDACommands jdaCommands;

    public CommandDispatcher(Object jda,
                             boolean isShardManager,
                             CommandSettings settings,
                             EventParser eventParser,
                             CommandMapper commandMapper,
                             ArgumentParser argumentParser,
                             EmbedFactory embedFactory,
                             HelpMessageSender helpMessageSender,
                             List<Provider> providers) {
        log.info("Starting JDA-Commands...");
        if (isActive) {
            throw new IllegalStateException("An instance of the command framework is already running!");
        }
        this.jda = jda;
        this.settings = settings;
        this.isShardManager = isShardManager;
        this.eventParser = eventParser;
        this.commandMapper = commandMapper;
        this.argumentParser = argumentParser;
        this.embedFactory = new EmbedFactory();
        this.helpMessageSender = helpMessageSender;
        commands = new CommandList();
        dependencyInjector = new DependencyInjector();
        commandRegistry = new CommandRegistry(settings, dependencyInjector);
        providers.forEach(dependencyInjector::addProvider);
        isActive = true;
    }

    public void start(JDACommands jdaCommands) {
        if (isShardManager) {
            ((ShardManager) jda).addEventListener(this);
        } else {
            ((JDA) jda).addEventListener(this);
        }
        this.jdaCommands = jdaCommands;
        commandRegistry.indexCommands();
        commands.addAll(commandRegistry.getCommands());
        dependencyInjector.inject();
        log.info("Finished loading!");
    }

    public void shutdown() {
        if (isShardManager) {
            ((ShardManager) jda).removeEventListener(this);
        } else {
            ((JDA) jda).removeEventListener(this);
        }
        commands.clear();
        isActive = false;
    }

    public CommandSettings getSettings() {
        return settings;
    }

    public CommandList getCommands() {
        return commands;
    }

    @Override
    @SubscribeEvent
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (!eventParser.validateEvent(event, settings)) {
            return;
        }

        String[] input = eventParser.parseEvent(event, settings);

        if (input.length == 1 && input[0].isEmpty()) {
            return;
        }

        if (settings.getHelpLabels().stream().anyMatch(s -> s.startsWith(input[0]))) {
            Optional<CommandCallable> command = commandMapper.findCommand(commands, Arrays.copyOfRange(input, 1, input.length), settings.isIgnoreLabelCase());
            if (command.isPresent()) {
                log.info("Executing specific help command for {}", event.getAuthor());
                helpMessageSender.sendSpecificHelp(event, embedFactory, settings, command.get());
            } else {
                log.info("Executing default help command for {}", event.getAuthor());
                helpMessageSender.sendDefaultHelp(event, embedFactory, settings, commands);
            }
            return;
        }

        Optional<CommandCallable> command = commandMapper.findCommand(commands, input, settings.isIgnoreLabelCase());
        if (!command.isPresent()) {
            log.debug("No command for input {} found", Arrays.toString(input));

            event.getChannel().sendMessage(embedFactory.getCommandNotFoundEmbed(settings, event)).queue();
            return;
        }
        CommandCallable commandCallable = command.get();

        if (!eventParser.hasPermission(commandCallable, event, settings)) {
            log.debug("{} has insufficient permissions for executing command {}", event.getAuthor(), commandCallable.getMethod().getName());
            event.getChannel().sendMessage(embedFactory.getInsufficientPermissionsEmbed(commandCallable, settings, event)).queue();
            return;
        }

        int from = commandCallable.getLabels().get(0).split(" ").length;
        List<String> rawArguments = Arrays.asList(Arrays.copyOfRange(input, from, input.length));

        Optional<List<Object>> parsedArguments = argumentParser.parseArguments(commandCallable, event, rawArguments, jdaCommands);
        if (!parsedArguments.isPresent()) {
            log.debug("Argument parsing for command {} failed. Expected {} but got {}",
                    commandCallable.getMethod().getName(),
                    commandCallable.getParameters(),
                    rawArguments);
            event.getChannel().sendMessage(embedFactory.getSyntaxErrorEmbed(commandCallable, rawArguments, settings, event)).queue();
            return;
        }
        log.info("Executing command {} for {}", commandCallable.getMethod().getName(), event.getAuthor());
        try {
            log.debug("Invoking method with following arguments {}", parsedArguments.get());
            commandCallable.getMethod().invoke(commandCallable.getControllerInstance(), parsedArguments.get().toArray());
        } catch (Exception e) {
            throw new CommandException("Command execution failed!", e);
        }
    }
}