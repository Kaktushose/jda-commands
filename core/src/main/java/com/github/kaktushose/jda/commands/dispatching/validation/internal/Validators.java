package com.github.kaktushose.jda.commands.dispatching.validation.internal;

import com.github.kaktushose.jda.commands.annotations.constraints.Constraint;
import com.github.kaktushose.jda.commands.annotations.constraints.NotPerm;
import com.github.kaktushose.jda.commands.annotations.constraints.Perm;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.dispatching.validation.impl.NotPermissionValidator;
import com.github.kaktushose.jda.commands.dispatching.validation.impl.PermissionValidator;

import java.lang.annotation.Annotation;
import java.util.*;

/// Central registry for all [Validator]s.
///
/// @see Validator
public class Validators {
    private final Map<Class<? extends Annotation>, Validator> validators;

    /// Constructs a new Validators. This will register the following [Validator]s by default:
    ///
    ///   - [PermissionValidator]
    ///   - [NotPermissionValidator]
    public Validators(Map<Class<? extends Annotation>, Validator> validators) {
        HashMap<Class<? extends Annotation>, Validator> validatorMap = new HashMap<>(validators);
        validatorMap.putAll(Map.of(
                // jda specific
                Perm.class, new PermissionValidator(),
                NotPerm.class, new NotPermissionValidator()
        ));
        this.validators = Collections.unmodifiableMap(validatorMap);
    }

    /// Gets a [Validator] based on the annotation and the type to validate. Returns an empty [Optional] if
    /// the [Validator] cannot validate the given type.
    ///
    /// @param annotation the class of the annotation
    /// @param type       the type to validate
    /// @return an [Optional] holding the [Validator]
    
    public Optional<Validator> get(Class<?> annotation, Class<?> type) {
        Validator validator = validators.get(annotation);

        if (validator == null || !annotation.isAnnotationPresent(Constraint.class)) {
            return Optional.empty();
        }

        Constraint constraint = annotation.getAnnotation(Constraint.class);
        if (Arrays.stream(constraint.value()).noneMatch(t -> t.isAssignableFrom(type))) {
            return Optional.empty();
        }

        return Optional.of(validator);
    }
}
