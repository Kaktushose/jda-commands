package com.github.kaktushose.jda.commands.exceptions;

import com.github.kaktushose.jda.commands.exceptions.internal.JDACException;
import com.github.kaktushose.jda.commands.message.placeholder.Entry;

import java.util.Locale;
import java.util.Map;

/// Will be thrown if anything goes wrong internally. Should be reported to the devs.
public final class InternalException extends JDACException {

    /// @param key the bundle key of the error message
    public InternalException(String key) {
        super(key);
    }

    /// @param key         the bundle key of the error message
    /// @param placeholder the placeholders to insert
    public InternalException(String key, Entry... placeholder) {
        super(key, placeholder);
    }

    /// @param key   the bundle key of the error message
    /// @param cause the cause of the internal exception
    public InternalException(String key, Throwable cause) {
        super(key, cause);
    }

    @Override
    public String getMessage() {
        return "%s %s".formatted(super.getMessage(), errorMessages.apply(Locale.ENGLISH, "internal-error", Map.of()));
    }
}
