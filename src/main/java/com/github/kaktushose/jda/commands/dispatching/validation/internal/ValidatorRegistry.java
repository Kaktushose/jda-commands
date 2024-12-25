package com.github.kaktushose.jda.commands.dispatching.validation.internal;

import com.github.kaktushose.jda.commands.annotations.constraints.*;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.dispatching.validation.impl.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/// Central registry for all [Validator]s.
///
/// @see Validator
public class ValidatorRegistry {

    private final Logger log = LoggerFactory.getLogger(ValidatorRegistry.class);
    private final Map<Class<? extends Annotation>, Validator> validators;

    /// Constructs a new ValidatorRegistry. This will register the following [Validator]s by default:
    ///
    ///   - [MinimumValidator]
    ///   - [MaximumValidator]
    ///   - [RoleValidator]
    ///   - [NotRoleValidator]
    ///   - [PermissionValidator]
    ///   - [NotPermissionValidator]
    ///   - [UserValidator]
    ///   - [NotUserValidator]
    public ValidatorRegistry() {
        validators = new HashMap<>();
        // default types
        register(Min.class, new MinimumValidator());
        register(Max.class, new MaximumValidator());

        // jda specific
        register(Role.class, new RoleValidator());
        register(NotRole.class, new NotRoleValidator());
        register(Perm.class, new PermissionValidator());
        register(NotPerm.class, new NotPermissionValidator());
        register(User.class, new UserValidator());
        register(NotUser.class, new NotUserValidator());
    }

    /// Register a [Validator] and map it to an annotation. Each annotation can only map to one [Validator].
    ///
    /// @param annotation the type of the annotation to map the [Validator] to
    /// @param validator  the [Validator] to map
    /// @throws IllegalArgumentException if the annotation class isn't annotated with [Constraint]
    public void register(@NotNull Class<? extends Annotation> annotation, @NotNull Validator validator) {
        if (!annotation.isAnnotationPresent(Constraint.class)) {
            throw new IllegalArgumentException(Constraint.class.getCanonicalName() + " annotation must be present!");
        }
        validators.put(annotation, validator);
        log.debug("Registered validator {} for annotation {}", validator.getClass().getName(), annotation.getName());
    }

    /// Unregisters the [Validator] mapped to the given annotation.
    ///
    /// @param annotation the class of the annotation to unregister
    public void unregister(Class<?> annotation) {
        validators.remove(annotation);
        log.debug("Unregistered validator for annotation {}", annotation.getName());
    }

    /// Gets a [Validator] based on the annotation and the type to validate. Returns an empty [Optional] if
    /// the [Validator] cannot validate the given type.
    ///
    /// @param annotation the class of the annotation
    /// @param type       the type to validate
    /// @return an [Optional] holding the [Validator]
    public Optional<Validator> get(@NotNull Class<?> annotation, @NotNull Class<?> type) {
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
