package io.github.kaktushose.jdac.exceptions;

import io.github.kaktushose.jdac.definitions.description.MethodDescription;
import io.github.kaktushose.jdac.definitions.description.ParameterDescription;
import io.github.kaktushose.jdac.exceptions.internal.JDACException;
import io.github.kaktushose.jdac.message.placeholder.Entry;

import java.util.stream.Collectors;

/// Will be thrown if any errors are made in the declaration of interactions.
public final class InvalidDeclarationException extends JDACException {

    public static final ScopedValue<MethodDescription> CONTEXT = ScopedValue.newInstance();
    private final String prefix;

    /// @param key the bundle key of the error message
    public InvalidDeclarationException(String key) {
        super(key);
        prefix = cause();
    }

    /// @param key         the key of the error message
    /// @param placeholder the [placeholders][Entry] to insert
    public InvalidDeclarationException(String key, Entry... placeholder) {
        super(key, placeholder);
        prefix = cause();
    }

    /// @param key         the bundle key of the error message
    /// @param cause       the cause of the exception
    /// @param placeholder the placeholders to insert
    public InvalidDeclarationException(String key, Throwable cause, Entry... placeholder) {
        super(key, cause, placeholder);
        prefix = cause();
    }

    private String cause() {
        if (CONTEXT.isBound()) {
            MethodDescription method = CONTEXT.get();

            return "Error while constructing definition of method '%s#%s(%s)': ".formatted(
                    method.declaringClass().getSimpleName(),
                    method.name(),
                    method.parameters().stream().map(ParameterDescription::type).map(Class::getSimpleName).collect(Collectors.joining(", "))
            );
        }
        return "";
    }

    @Override
    public String getMessage() {
        return prefix + super.getMessage();
    }
}

