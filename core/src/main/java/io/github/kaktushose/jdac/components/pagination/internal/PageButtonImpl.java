package io.github.kaktushose.jdac.components.pagination.internal;

import io.github.kaktushose.jdac.components.pagination.layout.PageButton;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;

public record PageButtonImpl(
        ActionRowChildComponent component,
        Direction direction,
        int amount,
        int threshold
) implements PageButton {

    public PageButtonImpl(ActionRowChildComponent component, Direction direction) {
        this(component, direction, 1, 1);
    }

    public PageButtonImpl(ActionRowChildComponent component, Direction direction, int amount) {
        this(component, direction, amount, 1);
    }

    @Override
    public PageButton threshold(int threshold) {
        return new PageButtonImpl(component, direction, amount, threshold);
    }

    @Override
    public PageButton direction(Direction direction) {
        return new PageButtonImpl(component, direction, amount, threshold);
    }

    @Override
    public PageButton amount(int amount) {
        return new PageButtonImpl(component, direction, amount, threshold);
    }
}
