package com.github.kaktushose.jda.commands.definitions.description;

import java.util.function.Function;

/// A [Descriptor] takes a [Class] as input and transforms it into a [ClassDescription].
@FunctionalInterface
public interface Descriptor extends Function<Class<?>, ClassDescription> {
}
