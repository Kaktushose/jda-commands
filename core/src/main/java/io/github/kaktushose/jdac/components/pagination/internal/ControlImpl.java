package io.github.kaktushose.jdac.components.pagination.internal;

import io.github.kaktushose.jdac.components.pagination.layout.Control;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;

public record ControlImpl(ActionRowChildComponent component, int threshold) implements Control {

    public ControlImpl(ActionRowChildComponent component) {
        this(component, 1);
    }

    @Override
    public Control threshold(int threshold) {
        return new ControlImpl(component, threshold);
    }
}
