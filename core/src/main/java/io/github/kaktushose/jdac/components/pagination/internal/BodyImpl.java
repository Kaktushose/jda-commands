package io.github.kaktushose.jdac.components.pagination.internal;

import io.github.kaktushose.jdac.components.pagination.layout.Body;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;

import java.util.SequencedCollection;
import java.util.function.Function;

public record BodyImpl(Function<Integer, SequencedCollection<ContainerChildComponent>> bodyFunction) implements Body { }
