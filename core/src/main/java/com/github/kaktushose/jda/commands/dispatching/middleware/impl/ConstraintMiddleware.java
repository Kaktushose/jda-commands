package com.github.kaktushose.jda.commands.dispatching.middleware.impl;

import com.github.kaktushose.jda.commands.annotations.constraints.Constraint;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.events.Event;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.dispatching.validation.internal.Validators;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import io.github.kaktushose.proteus.Proteus;
import io.github.kaktushose.proteus.conversion.ConversionResult;
import io.github.kaktushose.proteus.mapping.Mapper;
import io.github.kaktushose.proteus.mapping.MappingResult;
import io.github.kaktushose.proteus.type.Type;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;
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

    private final ErrorMessageFactory errorMessageFactory;

    private static final Logger log = LoggerFactory.getLogger(ConstraintMiddleware.class);

    public ConstraintMiddleware(ErrorMessageFactory errorMessageFactory) {
        this.errorMessageFactory = errorMessageFactory;
    }

    /// Checks if all parameters fulfill their constraints. Will cancel the [InvocationContext] if a parameter
    /// constraint fails.
    ///
    /// @param context the [InvocationContext] to filter
    @SuppressWarnings("unchecked")
    @Override
    public void accept(@NotNull InvocationContext<?> context) {
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

                // TODO remove this, when proteus is upgraded
                Proteus.global().register(Type.of(Member.class), Type.of(Member.class), Mapper.uni((member, _) -> MappingResult.lossless(member)));

                Constraint constraintAnn = constraint.annotation().annotation(Constraint.class).orElseThrow();
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
