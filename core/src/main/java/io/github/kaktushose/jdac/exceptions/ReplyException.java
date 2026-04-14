package io.github.kaktushose.jdac.exceptions;

import io.github.kaktushose.jdac.exceptions.internal.JDACException;

/// Thrown if an interaction reply failed.
public final class ReplyException extends JDACException {

    /// Constructs a new ReplyException.
    ///
    /// @param key the placeholders to insert
    public ReplyException(String key) {
        super(key);
    }

    /// Constructs a new ReplyException.
    ///
    /// @param key   the placeholders to insert
    /// @param cause the cause of the reply exception
    public ReplyException(String key, Throwable cause) {
        super(key, cause);
    }
}
