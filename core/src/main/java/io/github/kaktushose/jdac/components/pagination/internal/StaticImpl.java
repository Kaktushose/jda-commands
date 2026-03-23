package io.github.kaktushose.jdac.components.pagination.internal;

import io.github.kaktushose.jdac.components.pagination.layout.Static;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;

import java.util.SequencedCollection;

public record StaticImpl(SequencedCollection<ContainerChildComponent> components) implements Static { }
