package com.github.kaktushose.jda.commands.rewrite.dispatching.validation;

import com.github.kaktushose.jda.commands.rewrite.annotations.constraints.*;
import com.github.kaktushose.jda.commands.rewrite.dispatching.validation.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ValidatorRegistry {

    private final Logger log = LoggerFactory.getLogger(ValidatorRegistry.class);
    private final Map<Class<? extends Annotation>, Validator> validators;

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

    public void register(Class<? extends Annotation> annotation, Validator validator) {
        if (!annotation.isAnnotationPresent(Constraint.class)) {
            throw new IllegalArgumentException(Constraint.class.getCanonicalName() + " annotation must be present!");
        }
        validators.put(annotation, validator);
        log.debug("Registered validator {} for annotation {}", validator.getClass().getName(), annotation.getName());
    }

    public void unregister(Class<?> annotation) {
        validators.remove(annotation);
        log.debug("Unregistered validator for annotation {}", annotation.getName());
    }

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
