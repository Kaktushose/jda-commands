package io.github.kaktushose.jdac.exceptions.internal;

import dev.goldmensch.fluava.Bundle;
import dev.goldmensch.fluava.Fluava;
import io.github.kaktushose.jdac.configuration.ExtensionException;
import io.github.kaktushose.jdac.exceptions.*;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

/// Base exception for all exceptions known to JDA-Commands.
///
/// @implNote Error messages can be loaded from a Fluava bundle file called "jdac_internal_en.ftl" located in the resources folder.
public sealed class JDACException extends RuntimeException
        permits ConfigurationException, InternalException, InvalidDeclarationException, ParsingException {

    protected static final Bundle errorMessages = Fluava.create(Locale.ENGLISH).loadBundle("jdac_internal");

    private final String key;
    private final Map<String, @Nullable Object> placeholder;

    /// Creates a new JDACException and loads the error message from the given key.
    ///
    /// @param key the key of the error message
    public JDACException(String key) {
        super();
        this.key = key;
        this.placeholder = Map.of();
    }

    /// Creates a new JDACException, loads the error message from the given key and inserts the placeholders.
    ///
    /// @param key         the key of the error message
    /// @param placeholder the [placeholders][Entry] to insert
    public JDACException(String key, Entry... placeholder) {
        super();
        this.key = key;
        this.placeholder = Entry.toMap(placeholder);
    }

    /// Creates a new JDACException and loads the error message from the given key and cause.
    ///
    /// @param key   the key of the error message
    /// @param cause the cause of the exception
    public JDACException(String key, Throwable cause) {
        super(cause);
        this.key = key;
        this.placeholder = Map.of();
    }

    /// Creates a new JDACException with the given cause, loads the error message from the given key and inserts
    /// the placeholders.
    ///
    /// @param key   the key of the error message
    /// @param cause the cause of the exception
    /// @param placeholder the [placeholders][Entry] to insert
    public JDACException(String key, Throwable cause, Entry... placeholder) {
        super(cause);
        this.key = key;
        this.placeholder = Entry.toMap(placeholder);
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
        return errorMessages.apply(Locale.ENGLISH, key, Entry.toMap(placeholder));
    }

    @Override
    public String getMessage() {
        return resolveMessage(key, placeholder);
    }

    private String resolveMessage(String key, Map<String, @Nullable Object> placeholder) {
        Bundle bundle = switch (this) {
            case ExtensionException e -> e.bundle();
            case JDACException _ -> errorMessages;
        };
        return bundle.apply(Locale.ENGLISH, key, placeholder);
    }
}
