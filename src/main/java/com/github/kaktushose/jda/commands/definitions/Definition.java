package com.github.kaktushose.jda.commands.definitions;

import org.jetbrains.annotations.NotNull;

public interface Definition {

    @NotNull
    default String definitionId() {
        return String.valueOf(toString().hashCode());
    }

    String displayName();

}
