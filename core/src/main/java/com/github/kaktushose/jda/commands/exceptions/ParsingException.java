package com.github.kaktushose.jda.commands.exceptions;

import com.github.kaktushose.jda.commands.exceptions.internal.JDACException;
import com.github.kaktushose.jda.commands.message.placeholder.Entry;

/// Thrown if the JSON parsing inside of [Embed][com.github.kaktushose.jda.commands.embeds.Embed] fails.
public final class ParsingException extends JDACException {

    /// @param placeholder the placeholders to insert
    /// @param cause the cause of the internal exception
    public ParsingException(Throwable cause, Entry... placeholder) {
        super("localization-json-error", cause, placeholder);
    }
}
