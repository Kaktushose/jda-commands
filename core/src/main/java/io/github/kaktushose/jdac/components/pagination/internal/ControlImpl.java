package io.github.kaktushose.jdac.components.pagination.internal;

import io.github.kaktushose.jdac.components.pagination.layout.Control;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;

public record ControlImpl(
        ActionRowChildComponent component,
        Direction direction,
        int amount,
        int threshold
) implements Control {

    public ControlImpl(ActionRowChildComponent component, Direction direction) {
        this(component, direction, 1, 1);
    }

    public ControlImpl(ActionRowChildComponent component, Direction direction, int amount) {
        this(component, direction, amount, 1);
    }

    @Override
    public Control threshold(int threshold) {
        return new ControlImpl(component, direction, amount, threshold);
    }

    @Override
    public Control direction(Direction direction) {
        return new ControlImpl(component, direction, amount, threshold);
    }

    @Override
    public Control amount(int amount) {
        return new ControlImpl(component, direction, amount, threshold);
    }
}
