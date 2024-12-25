package com.github.kaktushose.jda.commands.definitions;

import java.util.function.Predicate;

public interface Registry {

    void index();

    <T extends Definition> T find(Class<T> type, boolean internalError, Predicate<T> predicate);

}
