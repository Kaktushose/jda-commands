package com.github.kaktushose.jda.commands.message.placeholder;

public sealed interface Component {
    record VariableReference(String name) implements  Component {}
    record Literal(String value) implements Component {}
}
