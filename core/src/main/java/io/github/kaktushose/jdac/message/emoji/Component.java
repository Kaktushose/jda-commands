package io.github.kaktushose.jdac.message.emoji;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
sealed interface Component permits Component.EmojiReference, Component.Literal {
    record EmojiReference(String name) implements Component { }

    record Literal(String value) implements Component { }
}
