package com.github.kaktushose.jda.commands.exceptions;

import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.description.ParameterDescription;

import java.util.stream.Collectors;

/// Will be thrown if any errors are made in the declaration of interactions.
public final class InvalidDeclarationException extends RuntimeException {

    public static final ThreadLocal<MethodDescription> CONTEXT = new ThreadLocal<>();

    /// @param message the exception message to be displayed
    public InvalidDeclarationException(String message) {
        super(message);
    }

    /// @param message the exception message to be displayed
    /// @param placeholder the values to replace the placeholders (see [String#format(String, Object...)])
    public InvalidDeclarationException(String message, Object... placeholder) {
        super(message.formatted(placeholder));
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
