package com.github.kaktushose.jda.commands.definitions;

import java.util.function.Function;

public interface DefinitionBuildFunction<R extends Definition> extends Function<Class<?>, R> {
}
