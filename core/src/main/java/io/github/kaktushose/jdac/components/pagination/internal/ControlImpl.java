package io.github.kaktushose.jdac.components.pagination.internal;

import io.github.kaktushose.jdac.components.pagination.layout.Control;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponentUnion;

public record ControlImpl(ControlType type, ActionRowChildComponentUnion component, int threshold) implements Control {

    public ControlImpl(ControlType type, ActionRowChildComponentUnion component) {
        this(type, component, 1);
    }

    @Override
    public Control threshold(int threshold) {
        return new ControlImpl(type, component, threshold);
    }
}
