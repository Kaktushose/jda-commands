package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.internal.ButtonImpl;

public sealed interface Button extends Control permits ButtonImpl {

    Direction direction();

    Button direction(Direction direction);

    int amount();

    Button amount(int amount);

}
