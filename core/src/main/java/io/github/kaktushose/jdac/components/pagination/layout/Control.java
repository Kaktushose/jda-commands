package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.internal.ControlImpl;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;

public interface Control extends Threshold {

    static Control forward(ActionRowChildComponent component) {
        return new ControlImpl(component, Direction.FORWARD);
    }

    static Control forward(ActionRowChildComponent component, int amount) {
        return new ControlImpl(component, Direction.FORWARD, amount);
    }

    static Control backward(ActionRowChildComponent component) {
        return new ControlImpl(component, Direction.BACKWARD);
    }

    static Control backward(ActionRowChildComponent component, int amount) {
        return new ControlImpl(component, Direction.BACKWARD, amount);
    }

    static Control of(ActionRowChildComponent component) {
        return new ControlImpl(component, Direction.NEUTRAL);
    }

    @Override
    Control threshold(int threshold);

    Control direction(Direction direction);

    Control amount(int amount);

    ActionRowChildComponent component();

    enum Direction {
        FORWARD,
        NEUTRAL,
        BACKWARD
    }
}
