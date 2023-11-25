package com.github.kaktushose.jda.commands.dispatching.validation;

import com.github.kaktushose.jda.commands.annotations.constraints.*;
import com.github.kaktushose.jda.commands.dispatching.validation.impl.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Central registry for all {@link Validator Validators}.
 *
 * @see Validator
 * @since 2.0.0
 */
public class ValidatorRegistry {

    private final Logger log = LoggerFactory.getLogger(ValidatorRegistry.class);
    private final Map<Class<? extends Annotation>, Validator> validators;

    /**
     * Constructs a new ValidatorRegistry. This will register the following {@link Validator Validators} by default:
     * <ul>
     *     <li>{@link MinimumValidator}</li>
     *     <li>{@link MaximumValidator}</li>
     *     <li>{@link RoleValidator}</li>
     *     <li>{@link NotRoleValidator}</li>
     *     <li>{@link PermissionValidator}</li>
     *     <li>{@link NotPermissionValidator}</li>
     *     <li>{@link UserValidator}</li>
     *     <li>{@link NotUserValidator}</li>
     * </ul>
     */
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

    /**
     * Register a {@link Validator} and map it to an annotation. Each annotation can only map to one {@link Validator}.
     *
     * @param annotation the type of the annotation to map the {@link Validator} to
     * @param validator  the {@link Validator} to map
     * @throws IllegalArgumentException if the annotation class isn't annotated with {@link Constraint}
     */
    public void register(@NotNull Class<? extends Annotation> annotation, @NotNull Validator validator) {
        if (!annotation.isAnnotationPresent(Constraint.class)) {
            throw new IllegalArgumentException(Constraint.class.getCanonicalName() + " annotation must be present!");
        }
        validators.put(annotation, validator);
        log.debug("Registered validator {} for annotation {}", validator.getClass().getName(), annotation.getName());
    }

    /**
     * Unregisters the {@link Validator} mapped to the given annotation.
     *
     * @param annotation the class of the annotation to unregister
     */
    public void unregister(Class<?> annotation) {
        validators.remove(annotation);
        log.debug("Unregistered validator for annotation {}", annotation.getName());
    }

    /**
     * Gets a {@link Validator} based on the annotation and the type to validate. Returns an empty {@link Optional} if
     * the {@link Validator} cannot validate the given type.
     *
     * @param annotation the class of the annotation
     * @param type       the type to validate
     * @return an {@link Optional} holding the {@link Validator}
     */
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
