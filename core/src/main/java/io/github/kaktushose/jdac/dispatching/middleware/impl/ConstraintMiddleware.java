package io.github.kaktushose.jdac.dispatching.middleware.impl;

import io.github.kaktushose.jdac.annotations.constraints.Constraint;
import io.github.kaktushose.jdac.definitions.interactions.command.SlashCommandDefinition;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.dispatching.events.Event;
import io.github.kaktushose.jdac.dispatching.middleware.Middleware;
import io.github.kaktushose.jdac.dispatching.validation.Validator;
import io.github.kaktushose.jdac.dispatching.validation.internal.Validators;
import io.github.kaktushose.jdac.embeds.error.ErrorMessageFactory;
import io.github.kaktushose.proteus.Proteus;
import io.github.kaktushose.proteus.conversion.ConversionResult;
import io.github.kaktushose.proteus.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/// A [Middleware] implementation that will check the parameter constraints a [SlashCommandDefinition] might have.
///
/// @see Validators ValidatorRegistry
public class ConstraintMiddleware implements Middleware {

    private static final Logger log = LoggerFactory.getLogger(ConstraintMiddleware.class);
    private final ErrorMessageFactory errorMessageFactory;

    public ConstraintMiddleware(ErrorMessageFactory errorMessageFactory) {
        this.errorMessageFactory = errorMessageFactory;
    }

    /// Checks if all parameters fulfill their constraints. Will cancel the [InvocationContext] if a parameter
    /// constraint fails.
    ///
    /// @param context the [InvocationContext] to filter
    @SuppressWarnings("unchecked")
    @Override
    public void accept(InvocationContext<?> context) {
        if (!(context.definition() instanceof SlashCommandDefinition command)) return;

        var arguments = new ArrayList<>(context.arguments());
        arguments.removeIf(Event.class::isInstance);
        var commandOptions = List.copyOf(command.commandOptions());

        Validator.Context validatorContext = new Validator.Context(context, errorMessageFactory);

        log.debug("Applying parameter constraints...");
        for (int i = 0; i < arguments.size(); i++) {

            var argument = arguments.get(i);

            // an argument that is null cannot be validated
            if (argument == null) {
                continue;
            }

            var optionData = commandOptions.get(i);
            for (var constraint : optionData.constraints()) {
                log.debug("Found constraint {} for parameter {}", constraint, optionData.declaredType().getName());
                Validator<Object, Annotation> validator = (Validator<Object, Annotation>) constraint.validator();

                // TODO better exception
                Constraint constraintAnn = constraint.annotation().findAnnotation(Constraint.class).orElseThrow();
                Class<?>[] supportedTypes = constraintAnn.value();
                Object converted = Arrays.stream(supportedTypes)
                        .map(klass -> Proteus.global().convert(argument, Type.dynamic(argument), Type.of(klass)))
                        .filter(ConversionResult.Success.class::isInstance)
                        .findAny()
                        .map(result -> ((ConversionResult.Success<?>) result).value())
                        .orElseThrow(() ->
                                new IllegalArgumentException("Couldn't convert value (type: %s) of option %s of command %s to any of the following types supported by @%s: %s".formatted(
                                        argument.getClass().getName(), optionData.name(), command.name(), constraint.annotation().type().getName(), Arrays.stream(supportedTypes).map(Class::getName).collect(Collectors.joining(", "))
                                ))
                        );

                validator.apply(converted, constraint.annotation().value(), validatorContext);

                if (context.cancelled()) return;
            }
        }
        log.debug("All constraints passed");
    }
}
