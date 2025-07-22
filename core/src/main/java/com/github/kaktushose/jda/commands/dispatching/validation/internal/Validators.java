package com.github.kaktushose.jda.commands.dispatching.validation.internal;

import com.github.kaktushose.jda.commands.annotations.constraints.Constraint;
import com.github.kaktushose.jda.commands.annotations.constraints.NotPerm;
import com.github.kaktushose.jda.commands.annotations.constraints.Perm;
import com.github.kaktushose.jda.commands.definitions.description.AnnotationDescription;
import com.github.kaktushose.jda.commands.dispatching.middleware.impl.ConstraintMiddleware;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.dispatching.validation.impl.NotPermissionValidator;
import com.github.kaktushose.jda.commands.dispatching.validation.impl.PermissionValidator;
import io.github.kaktushose.proteus.Proteus;
import io.github.kaktushose.proteus.type.Type;

import java.lang.annotation.Annotation;
import java.util.*;

/// Central registry for all [Validator]s.
///
/// @implNote The business logic for checking a command's options is implemented by [ConstraintMiddleware]
///
/// @see Validator
public class Validators {
    private final Map<Class<? extends Annotation>, Validator<?, ?>> validators;

    /// Constructs a new Validators. This will register the following [Validator]s by default:
    ///
    ///   - [PermissionValidator]
    ///   - [NotPermissionValidator]
    public Validators(Map<Class<? extends Annotation>, Validator<?, ?>> validators) {
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
    public <T, A extends Annotation> Optional<Validator<T, A>> get(AnnotationDescription<A> annotation, Class<T> type) {
        Validator<T, A> validator = (Validator<T, A>) validators.get(annotation.type());

        if (validator == null || annotation.annotation(Constraint.class).isEmpty()) {
            return Optional.empty();
        }

        Constraint constraint = annotation.annotation(Constraint.class).orElseThrow();

        boolean typesCompatible = Arrays.stream(constraint.value())
                .anyMatch(klass -> Proteus.global().existsPath(Type.of(type), Type.of(klass)));

        if (!typesCompatible) return Optional.empty();

        return Optional.of(validator);
    }
}
