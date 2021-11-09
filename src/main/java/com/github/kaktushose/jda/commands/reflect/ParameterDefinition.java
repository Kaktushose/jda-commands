package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.Concat;
import com.github.kaktushose.jda.commands.annotations.Optional;
import com.github.kaktushose.jda.commands.annotations.constraints.Constraint;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterDefinition {

    private static final Map<Class<?>, Class<?>> TYPE_MAPPINGS = new HashMap<Class<?>, Class<?>>() {
        {
            put(byte.class, Byte.class);
            put(short.class, Short.class);
            put(int.class, Integer.class);
            put(long.class, Long.class);
            put(double.class, Double.class);
            put(float.class, Float.class);
            put(boolean.class, Boolean.class);
            put(char.class, Character.class);
        }
    };

    private final Class<?> type;
    private final boolean isConcat;
    private final boolean isOptional;
    private final String defaultValue;
    private final boolean isPrimitive;
    private final String name;
    private final List<ConstraintDefinition> constraints;

    private ParameterDefinition(Class<?> type,
                                boolean isConcat,
                                boolean isOptional,
                                String defaultValue,
                                boolean isPrimitive,
                                String name,
                                List<ConstraintDefinition> constraints) {
        this.type = type;
        this.isConcat = isConcat;
        this.isOptional = isOptional;
        this.defaultValue = defaultValue;
        this.isPrimitive = isPrimitive;
        this.name = name;
        this.constraints = constraints;
    }

    public static ParameterDefinition build(Parameter parameter, ValidatorRegistry registry) {
        if (parameter.isVarArgs()) {
            throw new IllegalArgumentException("VarArgs is not supported for parameters!");
        }

        Class<?> parameterType = parameter.getType();
        parameterType = TYPE_MAPPINGS.getOrDefault(parameterType, parameterType);

        final boolean isConcat = parameter.isAnnotationPresent(Concat.class);
        if (isConcat && !String.class.isAssignableFrom(parameterType)) {
            throw new IllegalArgumentException("Concat can only be applied to Strings!");
        }

        final boolean isOptional = parameter.isAnnotationPresent(Optional.class);
        String defaultValue = "";
        if (isOptional) {
            defaultValue = parameter.getAnnotation(Optional.class).value();
        }
        if (defaultValue.isEmpty()) {
            defaultValue = null;
        }

        // index constraints
        List<ConstraintDefinition> constraints = new ArrayList<>();
        for (Annotation annotation : parameter.getAnnotations()) {
            Class<?> annotationType = annotation.annotationType();
            if (!annotationType.isAnnotationPresent(Constraint.class)) {
                continue;
            }

            // annotation object is always different, so we cannot cast it. Thus, we need to get the custom error message via reflection
            String message = "";
            try {
                Method method = annotationType.getDeclaredMethod("message");
                message = (String) method.invoke(annotation);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
            }
            if (message.isEmpty()) {
                message = "Parameter validation failed";
            }

            java.util.Optional<Validator> optional = registry.get(annotationType, parameterType);
            if (optional.isPresent()) {
                constraints.add(new ConstraintDefinition(optional.get(), message, annotation));
            }
        }

        // this value is only used to determine if a default value must be present (primitives cannot be null)
        boolean usesPrimitives = TYPE_MAPPINGS.containsKey(parameter.getType());

        return new ParameterDefinition(
                parameterType,
                isConcat,
                isOptional,
                defaultValue,
                usesPrimitives,
                parameter.getName(),
                constraints
        );
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isConcat() {
        return isConcat;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public boolean isPrimitive() {
        return isPrimitive;
    }

    public List<ConstraintDefinition> getConstraints() {
        return constraints;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "{" +
                type.getName() +
                ", isConcat=" + isConcat +
                ", isOptional=" + isOptional +
                ", defaultValue='" + defaultValue + '\'' +
                ", isPrimitive=" + isPrimitive +
                ", name='" + name + '\'' +
                ", constraints=" + constraints +
                '}';
    }
}
