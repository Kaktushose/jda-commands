package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.internal.PageButtonImpl;
import io.github.kaktushose.jdac.components.pagination.internal.PageSelectImpl;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;

public sealed interface Control extends Threshold permits PageButton, PageSelect {

    static PageButton forward(ActionRowChildComponent component) {
        return new PageButtonImpl(component, Direction.FORWARD);
    }

    static PageButton forward(ActionRowChildComponent component, int amount) {
        return new PageButtonImpl(component, Direction.FORWARD, amount);
    }

    static PageButton backward(ActionRowChildComponent component) {
        return new PageButtonImpl(component, Direction.BACKWARD);
    }

    static PageButton backward(ActionRowChildComponent component, int amount) {
        return new PageButtonImpl(component, Direction.BACKWARD, amount);
    }

    static PageButton neutral(ActionRowChildComponent component) {
        return new PageButtonImpl(component, Direction.NEUTRAL);
    }

    static PageSelect select(ActionRowChildComponent component) {
        return new PageSelectImpl(component, "Page %d");
    }

    static PageSelect select(ActionRowChildComponent component, String format) {
        return new PageSelectImpl(component, format);
    }

    int threshold();

    ActionRowChildComponent component();

    enum Direction {
        FORWARD,
        NEUTRAL,
        BACKWARD
    }
}
