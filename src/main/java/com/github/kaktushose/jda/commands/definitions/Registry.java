package com.github.kaktushose.jda.commands.definitions;

import com.github.kaktushose.jda.commands.definitions.interactions.impl.AutoCompleteDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.command.CommandDefinition;

import java.util.Collection;
import java.util.function.Predicate;

public interface Registry {

    void index();

    <T extends Definition> T find(Class<T> type, boolean internalError, Predicate<T> predicate);

    Collection<CommandDefinition> getCommands();

    Collection<AutoCompleteDefinition> getAutoCompletes();
}
