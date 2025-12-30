package io.github.kaktushose.jdac.dispatching.validation.internal;

import io.github.kaktushose.jdac.annotations.constraints.Constraint;
import io.github.kaktushose.jdac.annotations.constraints.Max;
import io.github.kaktushose.jdac.annotations.constraints.Min;
import io.github.kaktushose.jdac.annotations.constraints.NotPerm;
import io.github.kaktushose.jdac.annotations.constraints.Perm;
import io.github.kaktushose.jdac.definitions.description.AnnotationDescription;
import io.github.kaktushose.jdac.dispatching.middleware.impl.ConstraintMiddleware;
import io.github.kaktushose.jdac.dispatching.validation.Validator;
import io.github.kaktushose.jdac.dispatching.validation.impl.NotPermissionValidator;
import io.github.kaktushose.jdac.dispatching.validation.impl.PermissionValidator;
import io.github.kaktushose.proteus.Proteus;
import io.github.kaktushose.proteus.type.Type;

import java.lang.annotation.Annotation;
import java.util.*;

/// Central registry for all [Validator]s.
///
/// @implNote The business logic for checking a commands options is implemented by [ConstraintMiddleware]
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
    public Result get(AnnotationDescription<? extends Annotation> annotation, Class<?> type) {
        Constraint constraint = annotation.annotation(Constraint.class);

        boolean typesCompatible = Arrays.stream(constraint.value())
                .anyMatch(klass -> Proteus.global().existsPath(Type.of(type), Type.of(klass)));
        if (!typesCompatible) {
            return new Result.UnsupportedType(Arrays.asList(constraint.value()));
        }

        if (annotation.type().equals(Min.class) || annotation.type().equals(Max.class)) {
            return new Result.DiscordHandled();
        }

        Validator<?, ?> validator = validators.get(annotation.type());
        if (validator == null) {
            return new Result.NotFound();
        }
        return new Result.Success(validator);
    }

    public sealed interface Result {

        record NotFound() implements Result {}

        record UnsupportedType(Collection<Class<?>> supportedTypes) implements Result {}

        record Success(Validator<?, ?> validator) implements Result {}

        /// This currently only applies to [Min] and [Max]
        record DiscordHandled() implements Result {}
    }
}
