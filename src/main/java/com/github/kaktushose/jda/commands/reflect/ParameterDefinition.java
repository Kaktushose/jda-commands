package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.Concat;
import com.github.kaktushose.jda.commands.annotations.Optional;
import com.github.kaktushose.jda.commands.annotations.constraints.Constraint;
import com.github.kaktushose.jda.commands.annotations.constraints.Max;
import com.github.kaktushose.jda.commands.annotations.constraints.Min;
import com.github.kaktushose.jda.commands.annotations.interactions.Choices;
import com.github.kaktushose.jda.commands.annotations.interactions.Param;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Representation of a command parameter.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see Concat
 * @see Optional
 * @see Constraint
 * @see Choices
 * @see Param
 * @since 2.0.0
 */
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

    private static final Map<Class<?>, OptionType> OPTION_TYPE_MAPPINGS = new HashMap<Class<?>, OptionType>() {
        {
            put(Byte.class, OptionType.STRING);
            put(Short.class, OptionType.STRING);
            put(Integer.class, OptionType.INTEGER);
            put(Long.class, OptionType.NUMBER);
            put(Double.class, OptionType.NUMBER);
            put(Float.class, OptionType.NUMBER);
            put(Boolean.class, OptionType.BOOLEAN);
            put(Character.class, OptionType.STRING);
            put(String.class, OptionType.STRING);
            put(String[].class, OptionType.STRING);
            put(User.class, OptionType.USER);
            put(Member.class, OptionType.USER);
            put(GuildChannel.class, OptionType.CHANNEL);
            put(GuildMessageChannel.class, OptionType.CHANNEL);
            put(ThreadChannel.class, OptionType.CHANNEL);
            put(TextChannel.class, OptionType.CHANNEL);
            put(NewsChannel.class, OptionType.CHANNEL);
            put(AudioChannel.class, OptionType.CHANNEL);
            put(VoiceChannel.class, OptionType.CHANNEL);
            put(StageChannel.class, OptionType.CHANNEL);
            put(Role.class, OptionType.ROLE);
        }
    };

    private static final Map<Class<?>, List<ChannelType>> CHANNEL_TYPE_RESTRICTIONS = new HashMap<Class<?>, List<ChannelType>>() {
        {
            put(GuildMessageChannel.class, Collections.singletonList(ChannelType.TEXT));
            put(ThreadChannel.class, Arrays.asList(
                    ChannelType.GUILD_NEWS_THREAD,
                    ChannelType.GUILD_PUBLIC_THREAD,
                    ChannelType.GUILD_PRIVATE_THREAD
            ));
            put(TextChannel.class, Collections.singletonList(ChannelType.TEXT));
            put(NewsChannel.class, Collections.singletonList(ChannelType.NEWS));
            put(AudioChannel.class, Collections.singletonList(ChannelType.VOICE));
            put(VoiceChannel.class, Collections.singletonList(ChannelType.VOICE));
            put(StageChannel.class, Collections.singletonList(ChannelType.STAGE));
        }
    };

    private final Class<?> type;
    private final boolean isConcat;
    private final boolean isOptional;
    private final String defaultValue;
    private final boolean isPrimitive;
    private final String name;
    private final String description;
    private final List<Choice> choices;
    private final List<ConstraintDefinition> constraints;

    private ParameterDefinition(@NotNull Class<?> type,
                                boolean isConcat,
                                boolean isOptional,
                                @Nullable String defaultValue,
                                boolean isPrimitive,
                                @NotNull String name,
                                @NotNull String description,
                                @NotNull List<Choice> choices,
                                @NotNull List<ConstraintDefinition> constraints) {
        this.type = type;
        this.isConcat = isConcat;
        this.isOptional = isOptional;
        this.defaultValue = defaultValue;
        this.isPrimitive = isPrimitive;
        this.name = name;
        this.description = description;
        this.choices = choices;
        this.constraints = constraints;
    }

    /**
     * Builds a new ParameterDefinition.
     *
     * @param parameter the {@link Parameter} of the command method
     * @param registry  an instance of the corresponding {@link ValidatorRegistry}
     * @return a new ParameterDefinition
     */
    @NotNull
    public static ParameterDefinition build(@NotNull Parameter parameter, @NotNull ValidatorRegistry registry) {
        if (parameter.isVarArgs()) {
            throw new IllegalArgumentException("VarArgs is not supported for parameters!");
        }

        Class<?> parameterType = parameter.getType();
        parameterType = TYPE_MAPPINGS.getOrDefault(parameterType, parameterType);

        // Concat
        final boolean isConcat = parameter.isAnnotationPresent(Concat.class);
        if (isConcat && !String.class.isAssignableFrom(parameterType)) {
            throw new IllegalArgumentException("Concat can only be applied to Strings!");
        }

        // Optional
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

        // Param
        String name = parameter.getName();
        String description = "empty description";
        if (parameter.isAnnotationPresent(Param.class)) {
            Param param = parameter.getAnnotation(Param.class);
            name = param.name().isEmpty() ? name : param.name();
            description = param.value();
        }

        List<Choice> choices = new ArrayList<>();
        // Options
        if (parameter.isAnnotationPresent(Choices.class)) {
            Choices opt = parameter.getAnnotation(Choices.class);
            for (String option : opt.value()) {
                String[] parsed = option.split(":", 2);
                if (parsed.length < 1) {
                    continue;
                }
                if (parsed.length < 2) {
                    choices.add(new Choice(parsed[0], parsed[0]));
                    continue;
                }
                choices.add(new Choice(parsed[0], parsed[1]));
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
                name,
                description,
                choices,
                constraints
        );
    }

    /**
     * Transforms this parameter definition to a {@link OptionData}.
     *
     * @return the transformed {@link OptionData}
     */
    public OptionData toOptionData() {
        OptionData optionData = new OptionData(
                OPTION_TYPE_MAPPINGS.getOrDefault(type, OptionType.STRING),
                name,
                description,
                !isOptional
        );

        optionData.addChoices(choices);

        constraints.stream().filter(constraint ->
                constraint.getAnnotation().getClass().isAssignableFrom(Min.class)
        ).findFirst().ifPresent(constraint -> optionData.setMinValue(((Min) constraint.getAnnotation()).value()));

        constraints.stream().filter(constraint ->
                constraint.getAnnotation().getClass().isAssignableFrom(Max.class)
        ).findFirst().ifPresent(constraint -> optionData.setMaxValue(((Max) constraint.getAnnotation()).value()));

        java.util.Optional.ofNullable(CHANNEL_TYPE_RESTRICTIONS.get(type)).ifPresent(optionData::setChannelTypes);

        return optionData;
    }

    /**
     * Gets the type of the parameter.
     *
     * @return the type of the parameter
     */
    @NotNull
    public Class<?> getType() {
        return type;
    }

    /**
     * Whether the parameter should be concatenated.
     *
     * @return {@code true} if the parameter should be concatenated
     */
    public boolean isConcat() {
        return isConcat;
    }

    /**
     * Whether the parameter is optional.
     *
     * @return {@code true} if the parameter is optional
     */
    public boolean isOptional() {
        return isOptional;
    }

    /**
     * Gets a possibly-null default value to use if the parameter is optional.
     *
     * @return a possibly-null default value
     */
    @Nullable
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Whether the type of the parameter is a primitive.
     *
     * @return {@code true} if the type of the parameter is a primitive
     */
    public boolean isPrimitive() {
        return isPrimitive;
    }

    /**
     * Gets a possibly-empty list of {@link ConstraintDefinition ConstraintDefinitions}.
     *
     * @return a possibly-empty list of {@link ConstraintDefinition ConstraintDefinitions}
     */
    @NotNull
    public List<ConstraintDefinition> getConstraints() {
        return constraints;
    }

    /**
     * Gets the parameter name.
     *
     * @return the parameter name
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Gets the parameter description. Only used for slash commands.
     *
     * @return the parameter description
     */
    @NotNull
    public String getDescription() {
        return description;
    }

    /**
     * Gets the parameter choices. Only used for slash commands.
     *
     * @return the parameter choices
     */
    public List<Choice> getChoices() {
        return choices;
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
