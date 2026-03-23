package io.github.kaktushose.jdac.components.pagination.internal;

import io.github.kaktushose.jdac.components.pagination.layout.Dynamic;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;

import java.util.SequencedCollection;
import java.util.function.BiFunction;

public record DynamicImpl(BiFunction<Integer, Integer, SequencedCollection<ContainerChildComponent>> bodyFunction) implements Dynamic { }
