package com.github.kaktushose.jda.commands.dispatching.refactor.context;

import com.github.kaktushose.jda.commands.dispatching.refactor.Runtime;
import com.github.kaktushose.jda.commands.dispatching.refactor.handling.HandlerContext;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.GenericCommandDefinition;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;

import java.util.ArrayList;
import java.util.List;

public final class CommandExecutionContext<T extends GenericCommandInteractionEvent, U extends GenericCommandDefinition> extends ExecutionContext<T, U> {

    private final List<Object> arguments;

    public CommandExecutionContext(T event, U definition, Runtime runtime, HandlerContext handlerContext) {
        super(event, definition, runtime, handlerContext);
        this.arguments = new ArrayList<>();
    }

    public List<Object> arguments() {
        return arguments;
    }
}
