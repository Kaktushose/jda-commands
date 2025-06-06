package com.github.kaktushose.jda.commands.definitions.description;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.Collection;

public record PackageDescription(
        @NotNull String name,
        @NotNull Collection<Annotation> annotations
) implements Description {}
