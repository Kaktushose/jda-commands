package com.github.kaktushose.jda.commands.exceptions;

import com.github.kaktushose.jda.commands.i18n.I18n;

/// Will be thrown if anything goes wrong while configuring jda-commands.
public final class ConfigurationException extends JDACException {

    /// @param key the bundle key of the error message
    public ConfigurationException(String key) {
        super(key);
    }

    /// @param key         the bundle key of the error message
    /// @param placeholder the placeholders to insert
    public ConfigurationException(String key, I18n.Entry... placeholder) {
        super(key, placeholder);
    }

    /// @param key   the bundle key of the error message
    /// @param cause the cause of the internal exception
    public ConfigurationException(String key, Throwable cause) {
        super(key, cause);
    }

    @Override
    public String getMessage() {
        return "Error while trying to configure jda-commands: " + super.getMessage();
    }
}
