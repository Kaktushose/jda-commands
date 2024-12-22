package com.github.kaktushose.jda.commands.dispatching;

public sealed interface ExpirationStrategy {
    ExpirationStrategy AFTER_15_MIN = new ExpirationStrategy.Inactivity(15);
    ExpirationStrategy EXPLICIT = new ExpirationStrategy.Explicit();

    record Inactivity(long afterMinutes) implements ExpirationStrategy {}
    record Explicit() implements ExpirationStrategy {}
}
