package com.github.kaktushose.jda.commands.dispatching.validation.internal;

import com.github.kaktushose.jda.commands.annotations.constraints.Constraint;
import com.github.kaktushose.jda.commands.annotations.constraints.NotPerm;
import com.github.kaktushose.jda.commands.annotations.constraints.Perm;
import com.github.kaktushose.jda.commands.definitions.description.AnnotationDescription;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.dispatching.validation.impl.NotPermissionValidator;
import com.github.kaktushose.jda.commands.dispatching.validation.impl.PermissionValidator;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.*;

/// Central registry for all [Validator]s.
///
/// @see Validator
public class Validators {
    private final Map<Class<? extends Annotation>, Validator<?, ?>> validators;

    /// Constructs a new Validators. This will register the following [Validator]s by default:
    ///
    ///   - [PermissionValidator]
    ///   - [NotPermissionValidator]
    public Validators(@NotNull Map<Class<? extends Annotation>, @NotNull Validator<?, ?>> validators) {
        HashMap<Class<? extends Annotation>, Validator<?, ?>> validatorMap = new HashMap<>(validators);
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
    @SuppressWarnings("unchecked")
    @NotNull
    public <T, A extends Annotation> Optional<Validator<T, A>> get(@NotNull AnnotationDescription<A> annotation, @NotNull Class<T> type) {
        Validator<T, A> validator = (Validator<T, A>) validators.get(annotation.type());

        if (validator == null || annotation.annotation(Constraint.class).isEmpty()) {
            return Optional.empty();
        }

        Constraint constraint = annotation.annotation(Constraint.class).orElseThrow();
        if (Arrays.stream(constraint.value()).noneMatch(t -> t.isAssignableFrom(type))) {
            return Optional.empty();
        }

        return Optional.of(validator);
    }
}
