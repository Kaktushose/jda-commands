package com.github.kaktushose.jda.commands.exceptions;

/// Will be thrown if anything goes wrong while configuring jda-commands.
public final class ConfigurationException extends JDACException {

    /// @param message the exception message to be displayed
    public ConfigurationException(String message) {
        super("Error while trying to configure jda-commands: " + message);
    }

    /// @param message     the exception message to be displayed
    /// @param placeholder the values to replace the placeholders (see [String#format(String, Object...) ])
    public ConfigurationException(String message, Object... placeholder) {
        super(message.formatted(placeholder));
    }
}
