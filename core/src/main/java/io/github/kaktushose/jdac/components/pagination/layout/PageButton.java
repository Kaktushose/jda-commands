package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.internal.PageButtonImpl;
import net.dv8tion.jda.api.components.buttons.Button;

public sealed interface PageButton extends Control<Button> permits PageButtonImpl {

    Direction direction();

    PageButton direction(Direction direction);

    int amount();

    PageButton amount(int amount);

}
