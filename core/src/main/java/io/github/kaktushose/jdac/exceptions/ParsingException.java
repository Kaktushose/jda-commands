package io.github.kaktushose.jdac.exceptions;

import io.github.kaktushose.jdac.exceptions.internal.JDACException;
import io.github.kaktushose.jdac.message.placeholder.Entry;

/// Thrown if the JSON parsing during localization of embeds or components fails.
public final class ParsingException extends JDACException {

    /// @param placeholder the placeholders to insert
    /// @param cause       the cause of the internal exception
    public ParsingException(Throwable cause, Entry... placeholder) {
        super("localization-json-error", cause, placeholder);
    }
}
