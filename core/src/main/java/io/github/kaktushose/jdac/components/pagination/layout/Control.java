package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.internal.PageButtonImpl;
import io.github.kaktushose.jdac.components.pagination.internal.PageSelectImpl;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;

public sealed interface Control extends Threshold permits PageButton, PageSelect {

    static PageButton forward(Button component) {
        return new PageButtonImpl(component, Direction.FORWARD);
    }

    static PageButton forward(Button component, int amount) {
        return new PageButtonImpl(component, Direction.FORWARD, amount);
    }

    static PageButton backward(Button component) {
        return new PageButtonImpl(component, Direction.BACKWARD);
    }

    static PageButton backward(Button component, int amount) {
        return new PageButtonImpl(component, Direction.BACKWARD, amount);
    }

    static PageButton neutral(Button component) {
        return new PageButtonImpl(component, Direction.NEUTRAL);
    }

    static PageSelect select(StringSelectMenu component) {
        return new PageSelectImpl(component, "Page %d");
    }

    static PageSelect select(StringSelectMenu component, String format) {
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
