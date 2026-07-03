package io.github.kaktushose.jdac.components.pagination.internal;

import io.github.kaktushose.jdac.components.pagination.Page;
import io.github.kaktushose.jdac.components.pagination.layout.Control;
import io.github.kaktushose.jdac.components.pagination.layout.PageButton;
import net.dv8tion.jda.api.components.buttons.Button;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Predicate;

@ApiStatus.Internal
public record PageButtonImpl(
        Button component,
        Direction direction,
        int amount,
        Predicate<Page> predicate
) implements PageButton {

    public PageButtonImpl(Button component, Direction direction) {
        this(component, direction, 1);
    }

    public PageButtonImpl(Button component, Direction direction, int amount) {
        this(component, direction, amount, _ -> true);
    }

    @Override
    public Control predicate(Predicate<Page> predicate) {
        return new PageButtonImpl(component, direction, amount, predicate);
    }

    @Override
    public PageButton direction(Direction direction) {
        return new PageButtonImpl(component, direction, amount, predicate);
    }

    @Override
    public PageButton amount(int amount) {
        return new PageButtonImpl(component, direction, amount, predicate);
    }
}
