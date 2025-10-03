package com.github.kaktushose.jda.commands.message.placeholder;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
sealed interface Component {
    record PlaceholderReference(String name) implements  Component {}
    record Literal(String value) implements Component {}
}
