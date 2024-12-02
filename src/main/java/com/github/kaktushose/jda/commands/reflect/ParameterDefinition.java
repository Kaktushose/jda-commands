package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.constraints.Constraint;
import com.github.kaktushose.jda.commands.annotations.constraints.Max;
import com.github.kaktushose.jda.commands.annotations.constraints.Min;
import com.github.kaktushose.jda.commands.annotations.interactions.Choices;
import com.github.kaktushose.jda.commands.annotations.interactions.Optional;
import com.github.kaktushose.jda.commands.annotations.interactions.Param;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
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

import static java.util.Map.entry;

/**
 * Representation of a command parameter.
 *
 * @see Constraint
 * @see Choices
 * @see Param
 * @since 2.0.0
 */
public record ParameterDefinition(
        Class<?> type,
        boolean isOptional,
        String defaultValue,
        boolean isPrimitive,
        String name,
        String description,
        List<Choice> choices,
        List<ConstraintDefinition> constraints
) {

    private static final Map<Class<?>, Class<?>> TYPE_MAPPINGS = Map.ofEntries(
            entry(byte.class, Byte.class),
            entry(short.class, Short.class),
            entry(int.class, Integer.class),
            entry(long.class, Long.class),
            entry(double.class, Double.class),
            entry(float.class, Float.class),
            entry(boolean.class, Boolean.class),
            entry(char.class, Character.class)
    );

    private static final Map<Class<?>, OptionType> OPTION_TYPE_MAPPINGS = Map.ofEntries(
            entry(Byte.class, OptionType.STRING),
            entry(Short.class, OptionType.STRING),
            entry(Integer.class, OptionType.INTEGER),
            entry(Long.class, OptionType.NUMBER),
            entry(Double.class, OptionType.NUMBER),
            entry(Float.class, OptionType.NUMBER),
            entry(Boolean.class, OptionType.BOOLEAN),
            entry(Character.class, OptionType.STRING),
            entry(String.class, OptionType.STRING),
            entry(String[].class, OptionType.STRING),
            entry(User.class, OptionType.USER),
            entry(Member.class, OptionType.USER),
            entry(GuildChannel.class, OptionType.CHANNEL),
            entry(GuildMessageChannel.class, OptionType.CHANNEL),
            entry(ThreadChannel.class, OptionType.CHANNEL),
            entry(TextChannel.class, OptionType.CHANNEL),
            entry(NewsChannel.class, OptionType.CHANNEL),
            entry(AudioChannel.class, OptionType.CHANNEL),
            entry(VoiceChannel.class, OptionType.CHANNEL),
            entry(StageChannel.class, OptionType.CHANNEL),
            entry(Role.class, OptionType.ROLE)
    );

    private static final Map<Class<?>, List<ChannelType>> CHANNEL_TYPE_RESTRICTIONS = Map.ofEntries(
            entry(GuildMessageChannel.class, Collections.singletonList(ChannelType.TEXT)),
            entry(TextChannel.class, Collections.singletonList(ChannelType.TEXT)),
            entry(NewsChannel.class, Collections.singletonList(ChannelType.NEWS)),
            entry(AudioChannel.class, Collections.singletonList(ChannelType.VOICE)),
            entry(VoiceChannel.class, Collections.singletonList(ChannelType.VOICE)),
            entry(StageChannel.class, Collections.singletonList(ChannelType.STAGE)),
            entry(ThreadChannel.class, Arrays.asList(
                    ChannelType.GUILD_NEWS_THREAD,
                    ChannelType.GUILD_PUBLIC_THREAD,
                    ChannelType.GUILD_PRIVATE_THREAD
            ))
    );

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
        name = name.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();

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
     * @param isAutoComplete whether this {@link OptionData} should support auto complete
     * @return the transformed {@link OptionData}
     */
    public OptionData toOptionData(boolean isAutoComplete) {
        OptionType optionType = OPTION_TYPE_MAPPINGS.getOrDefault(type, OptionType.STRING);

        OptionData optionData = new OptionData(
                optionType,
                name,
                description,
                !isOptional
        );

        optionData.addChoices(choices);
        if (optionType.canSupportChoices() && choices.isEmpty()) {
            optionData.setAutoComplete(isAutoComplete);
        }

        constraints.stream().filter(constraint ->
                constraint.annotation() instanceof Min
        ).findFirst().ifPresent(constraint -> optionData.setMinValue(((Min) constraint.annotation()).value()));

        constraints.stream().filter(constraint ->
                constraint.annotation() instanceof Max
        ).findFirst().ifPresent(constraint -> optionData.setMaxValue(((Max) constraint.annotation()).value()));

        java.util.Optional.ofNullable(CHANNEL_TYPE_RESTRICTIONS.get(type)).ifPresent(optionData::setChannelTypes);

        return optionData;
    }
}
