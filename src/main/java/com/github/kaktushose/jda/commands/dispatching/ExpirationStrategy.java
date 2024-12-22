package com.github.kaktushose.jda.commands.dispatching;

public sealed interface ExpirationStrategy {
    Inactivity AFTER_15_MIN = new ExpirationStrategy.Inactivity(15);

    record Inactivity(long afterMinutes) implements ExpirationStrategy {}
    record Explicit() implements ExpirationStrategy {}
}
