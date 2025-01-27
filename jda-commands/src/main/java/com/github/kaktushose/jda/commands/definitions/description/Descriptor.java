package com.github.kaktushose.jda.commands.definitions.description;

import com.github.kaktushose.jda.commands.extension.Implementation;

import java.util.function.Function;

/// A [Descriptor] takes a [Class] as input and transforms it into a [ClassDescription].
@FunctionalInterface
public non-sealed interface Descriptor extends Function<Class<?>, ClassDescription>, Implementation.ExtensionImplementable {
}
