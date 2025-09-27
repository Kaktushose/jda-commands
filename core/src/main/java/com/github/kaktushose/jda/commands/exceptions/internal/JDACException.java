package com.github.kaktushose.jda.commands.exceptions.internal;

import com.github.kaktushose.jda.commands.exceptions.ConfigurationException;
import com.github.kaktushose.jda.commands.exceptions.InternalException;
import com.github.kaktushose.jda.commands.exceptions.InvalidDeclarationException;
import com.github.kaktushose.jda.commands.message.placeholder.Entry;
import dev.goldmensch.fluava.Bundle;
import dev.goldmensch.fluava.Fluava;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/// Base exception for all exceptions thrown by JDA-Commands.
///
/// @implNote Error messages can be loaded from a Fluava bundle file called "jdac_en.ftl" located in the resources folder.
public sealed class JDACException extends RuntimeException
        permits ConfigurationException, InternalException, InvalidDeclarationException {

    protected static final Bundle errorMessages = new Fluava(Locale.ENGLISH, Map.of()).loadBundle("jdac");

    /// Creates a new JDACException and loads the error message from the given key.
    ///
    /// @param key the key of the error message
    public JDACException(String key) {
        super(errorMessage(key));
    }

    /// Creates a new JDACException, loads the error message from the given key and inserts the placeholders.
    ///
    /// @param key         the key of the error message
    /// @param placeholder the [placeholders][Entry] to insert
    public JDACException(String key, Entry... placeholder) {
        super(errorMessage(key, placeholder));
    }

    /// Creates a new JDACException and loads the error message from the given key and cause.
    ///
    /// @param key   the key of the error message
    /// @param cause the cause of the exception
    public JDACException(String key, Throwable cause) {
        super(errorMessages.apply(Locale.ENGLISH, key, Map.of()), cause);
    }

    /// Creates a new JDACException with the given cause, loads the error message from the given key and inserts
    /// the placeholders.
    ///
    /// @param key   the key of the error message
    /// @param cause the cause of the exception
    /// @param placeholder the [placeholders][Entry] to insert
    public JDACException(String key, Throwable cause, Entry... placeholder) {
        super(errorMessage(key, placeholder), cause);
    }

    /// Retrieves an error message from the error bundle.
    ///
    /// @param key the key of the error message
    /// @return the error message
    public static String errorMessage(String key) {
        return errorMessages.apply(Locale.ENGLISH, key, Map.of());
    }

    /// Retrieves an error message from the error bundle and inserts the placeholders.
    ///
    /// @param key         the key of the error message
    /// @param placeholder the [placeholders][Entry] to insert
    /// @return the error message
    public static String errorMessage(String key, Entry... placeholder) {
        return errorMessages.apply(Locale.ENGLISH, key,
                Arrays.stream(placeholder).collect(Collectors.toUnmodifiableMap(Entry::name, Entry::value))
        );
    }
}
