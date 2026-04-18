package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.internal.ButtonImpl;
import io.github.kaktushose.jdac.components.pagination.internal.PageSelectImpl;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;

public sealed interface Control extends Threshold permits Button, PageSelect {

    static Button forward(ActionRowChildComponent component) {
        return new ButtonImpl(component, Direction.FORWARD);
    }

    static Button forward(ActionRowChildComponent component, int amount) {
        return new ButtonImpl(component, Direction.FORWARD, amount);
    }

    static Button backward(ActionRowChildComponent component) {
        return new ButtonImpl(component, Direction.BACKWARD);
    }

    static Button backward(ActionRowChildComponent component, int amount) {
        return new ButtonImpl(component, Direction.BACKWARD, amount);
    }

    static Button neutral(ActionRowChildComponent component) {
        return new ButtonImpl(component, Direction.NEUTRAL);
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
