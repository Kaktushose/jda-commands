package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.reflect.CommandDefinition;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class CommandContext {

    private String[] input;
    private MessageReceivedEvent event;
    private CommandDefinition command;
    private List<Object> arguments;
    private Message errorMessage;
    private GuildSettings settings;
    private ImplementationRegistry registry;
    private JDACommands jdaCommands;
    private boolean isHelpEvent;
    private boolean cancelled;

    public String[] getInput() {
        return input;
    }

    public CommandContext setInput(String[] input) {
        this.input = input;
        return this;
    }

    public MessageReceivedEvent getEvent() {
        return event;
    }

    public CommandContext setEvent(MessageReceivedEvent event) {
        this.event = event;
        return this;
    }

    public CommandDefinition getCommand() {
        return command;
    }

    public CommandContext setCommand(CommandDefinition command) {
        this.command = command;
        return this;
    }

    public List<Object> getArguments() {
        return arguments;
    }

    public void setArguments(List<Object> arguments) {
        this.arguments = arguments;
    }

    public Message getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(Message message) {
        this.errorMessage = message;
    }

    public GuildSettings getSettings() {
        return settings;
    }

    public CommandContext setSettings(GuildSettings settings) {
        this.settings = settings;
        return this;
    }

    public ImplementationRegistry getImplementationRegistry() {
        return registry;
    }

    public CommandContext setImplementationRegistry(ImplementationRegistry registry) {
        this.registry = registry;
        return this;
    }

    public JDACommands getJdaCommands() {
        return jdaCommands;
    }

    public CommandContext setJdaCommands(JDACommands jdaCommands) {
        this.jdaCommands = jdaCommands;
        return this;
    }

    public boolean isHelpEvent() {
        return isHelpEvent;
    }

    public void setHelpEvent(boolean helpEvent) {
        isHelpEvent = helpEvent;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public CommandContext setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
        return this;
    }
}