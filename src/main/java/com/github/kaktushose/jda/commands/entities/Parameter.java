package com.github.kaktushose.jda.commands.entities;

import com.github.kaktushose.jda.commands.internal.ParameterType;

import javax.annotation.Nonnull;

/**
 * This class represents a parameter defined in the signature of a command method.
 *
 * @author Kaktushose
 * @version 1.0.0
 * @see com.github.kaktushose.jda.commands.annotations.Command
 * @see com.github.kaktushose.jda.commands.annotations.Optional
 * @see com.github.kaktushose.jda.commands.annotations.Concat
 * @since 1.0.0
 */
public class Parameter {

    private final boolean isConcat;
    private final boolean isOptional;
    private final String defaultValue;
    private final ParameterType parameterType;

    /**
     * Constructs a new Parameter.
     *
     * @param isConcat      true if the parameter should be concatenated
     * @param isOptional    true if the parameter is optional
     * @param defaultValue  the default value for the parameter, only used if isOptional is true
     * @param parameterType the {@link ParameterType} of the parameter
     */

    public Parameter(boolean isConcat, boolean isOptional, @Nonnull String defaultValue, @Nonnull ParameterType parameterType) {
        this.isConcat = isConcat;
        this.isOptional = isOptional;
        this.defaultValue = defaultValue;
        this.parameterType = parameterType;
    }

    /**
     * Whether the parameter should be concatenated or not.
     *
     * @return {@code true} if the parameter should be concatenated
     * @see com.github.kaktushose.jda.commands.annotations.Concat
     */
    public boolean isConcat() {
        return isConcat;
    }

    /**
     * Whether the parameter is optional or not.
     *
     * @return {@code true} if the parameter is optional
     * @see com.github.kaktushose.jda.commands.annotations.Optional
     */
    public boolean isOptional() {
        return isOptional;
    }

    /**
     * Get the default value of the parameter
     *
     * @return default value
     * @see com.github.kaktushose.jda.commands.annotations.Optional
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Get the {@link ParameterType} of the parameter
     *
     * @return the {@link ParameterType}
     */
    public ParameterType getParameterType() {
        return parameterType;
    }

    @Override
    public String toString() {
        return parameterType.name;
    }
}
