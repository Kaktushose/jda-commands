package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.internal.PageButtonImpl;

public sealed interface PageButton extends Control permits PageButtonImpl {

    Direction direction();

    PageButton direction(Direction direction);

    int amount();

    PageButton amount(int amount);

}
