package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.Pagination;
import io.github.kaktushose.jdac.components.pagination.internal.PageButtonImpl;
import net.dv8tion.jda.api.components.buttons.Button;

/// When the direction is set to [Direction#BACKWARD], the [PageButton] will
/// automatically be disabled on the first page. [Direction#FORWARD] will disable the [PageButton] on the last page.
/// See [Pagination] for details on how to set the last page. [Direction#NEUTRAL] will keep the [PageButton] always
/// enabled.
public sealed interface PageButton extends Control<Button> permits PageButtonImpl {

    Direction direction();

    PageButton direction(Direction direction);

    int amount();

    PageButton amount(int amount);

}
