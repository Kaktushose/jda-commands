package com.github.kaktushose.jda.commands.definitions.interactions.impl;

import com.github.kaktushose.jda.commands.definitions.interactions.Interaction;

import java.lang.reflect.Method;
import java.util.SequencedCollection;
import java.util.Set;

public record AutoCompleteDefinition(
        String displayName,
        Method method,
        SequencedCollection<Class<?>> parameters,
        Set<String> commands
) implements Interaction {}
