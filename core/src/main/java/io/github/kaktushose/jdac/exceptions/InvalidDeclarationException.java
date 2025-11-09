package io.github.kaktushose.jdac.exceptions;

import io.github.kaktushose.jdac.definitions.description.MethodDescription;
import io.github.kaktushose.jdac.definitions.description.ParameterDescription;
import io.github.kaktushose.jdac.exceptions.internal.JDACException;
import io.github.kaktushose.jdac.message.placeholder.Entry;

import java.util.stream.Collectors;

/// Will be thrown if any errors are made in the declaration of interactions.
public final class InvalidDeclarationException extends JDACException {

    public static final ScopedValue<MethodDescription> CONTEXT = ScopedValue.newInstance();

    /// @param key the bundle key of the error message
    public InvalidDeclarationException(String key) {
        super(key);
    }

    /// @param key         the bundle key of the error message
    /// @param placeholder the placeholders to insert
    public InvalidDeclarationException(String key, Entry... placeholder) {
        super(key, placeholder);
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public String getMessage() {
        if (CONTEXT.get() == null) return super.getMessage();
        MethodDescription method = CONTEXT.get();

        String prefix = "Error while constructing definition of method '%s#%s(%s)': ".formatted(
                method.declaringClass().getName(),
                method.name(),
                method.parameters().stream().map(ParameterDescription::type).map(Class::getName).collect(Collectors.joining(", "))
        );

        return prefix + super.getMessage();
    }
}
