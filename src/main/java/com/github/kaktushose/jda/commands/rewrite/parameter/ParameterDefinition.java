package com.github.kaktushose.jda.commands.rewrite.parameter;

import com.github.kaktushose.jda.commands.annotations.Concat;
import com.github.kaktushose.jda.commands.annotations.Optional;
import com.github.kaktushose.jda.commands.rewrite.validation.Validator;

import java.lang.reflect.Parameter;
import java.util.Collections;
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
    private final List<Validator> validators;

    private ParameterDefinition(Class<?> type,
                                boolean isConcat,
                                boolean isOptional,
                                String defaultValue,
                                List<Validator> validators) {
        this.type = type;
        this.isConcat = isConcat;
        this.isOptional = isOptional;
        this.defaultValue = defaultValue;
        this.validators = validators;
    }

    public ParameterDefinition build(Parameter parameter) {
        if (parameter.isVarArgs()) {
            throw new IllegalArgumentException("VarArgs is not supported for parameters");
        }

        final boolean isConcat = parameter.isAnnotationPresent(Concat.class);

        final boolean isOptional = parameter.isAnnotationPresent(Optional.class);

        final String defaultValue = parameter.getAnnotation(Optional.class).value();

        // TODO validator parsing

        return new ParameterDefinition(
                TYPE_MAPPINGS.getOrDefault(parameter.getType(), parameter.getType()),
                isConcat,
                isOptional,
                defaultValue,
                Collections.emptyList()
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

    public List<Validator> getValidators() {
        return validators;
    }
}
