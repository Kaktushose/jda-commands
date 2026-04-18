package io.github.kaktushose.jdac.components.pagination.internal;

import io.github.kaktushose.jdac.components.pagination.layout.Button;
import io.github.kaktushose.jdac.components.pagination.layout.Control;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;

public record ButtonImpl(
        ActionRowChildComponent component,
        Direction direction,
        int amount,
        int threshold
) implements Button {

    public ButtonImpl(ActionRowChildComponent component, Direction direction) {
        this(component, direction, 1, 1);
    }

    public ButtonImpl(ActionRowChildComponent component, Direction direction, int amount) {
        this(component, direction, amount, 1);
    }

    @Override
    public Button threshold(int threshold) {
        return new ButtonImpl(component, direction, amount, threshold);
    }

    @Override
    public Button direction(Direction direction) {
        return new ButtonImpl(component, direction, amount, threshold);
    }

    @Override
    public Button amount(int amount) {
        return new ButtonImpl(component, direction, amount, threshold);
    }
}
