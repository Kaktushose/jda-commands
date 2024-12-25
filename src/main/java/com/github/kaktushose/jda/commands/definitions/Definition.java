package com.github.kaktushose.jda.commands.definitions;

public interface Definition {

    default String definitionId() {
        return String.valueOf(toString().hashCode());
    }

    String displayName();

}
