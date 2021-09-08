package com.github.kaktushose.jda.commands.rewrite.dispatching;

import com.github.kaktushose.jda.commands.rewrite.reflect.CommandDefinition;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class CommandContext {

    private String[] input;
    private MessageReceivedEvent event;
    private CommandDefinition command;
    private List<Object> arguments;
    private Message errorMessage;
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

    public boolean isCancelled() {
        return cancelled;
    }

    public CommandContext setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
        return this;
    }
}
