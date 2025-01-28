package com.github.kaktushose.jda.commands.annotations.interactions;

/// Enum describing the two possible scopes a command can be registered for.
///
/// @see SlashCommand#scope()
/// @see ContextCommand#scope()
public enum CommandScope {
    GUILD,
    GLOBAL
}
