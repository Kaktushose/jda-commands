package com.github.kaktushose.jda.commands.definitions;

import com.github.kaktushose.jda.commands.annotations.constraints.Constraint;
import com.github.kaktushose.jda.commands.annotations.constraints.Max;
import com.github.kaktushose.jda.commands.annotations.constraints.Min;
import com.github.kaktushose.jda.commands.annotations.interactions.Choices;
import com.github.kaktushose.jda.commands.annotations.interactions.Param;
import com.github.kaktushose.jda.commands.definitions.description.ParameterDescription;
import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
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
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static java.util.Map.entry;

public record ParameterDefinition(
        Class<?> type,
        boolean optional,
        boolean autoComplete,
        String defaultValue,
        boolean primitive,
        String name,
        String description,
        SequencedCollection<Command.Choice> choices,
        Collection<ConstraintDefinition> constraints
) implements Definition, JDAEntity<OptionData> {

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

    public static ParameterDefinition build(ParameterDescription parameter,
                                                      boolean autoComplete,
                                                      @NotNull ValidatorRegistry validatorRegistry) {
        final var parameterType = TYPE_MAPPINGS.getOrDefault(parameter.type(), parameter.type());

        var optional = parameter.annotation(com.github.kaktushose.jda.commands.annotations.interactions.Optional.class);
        var defaultValue = "";
        if (optional.isPresent()) {
            defaultValue = optional.get().value();
        }
        if (defaultValue.isEmpty()) {
            defaultValue = null;
        }

        // index constraints
        List<ConstraintDefinition> constraints = new ArrayList<>();
        parameter.annotations().stream()
                .filter(it -> it.annotationType().isAnnotationPresent(Constraint.class))
                .forEach(it -> {
                    var validator = validatorRegistry.get(it.annotationType(), parameterType);
                    validator.ifPresent(value -> constraints.add(ConstraintDefinition.build(value, it)));
                });

        // Param
        String name = parameter.name();
        String description = "empty description";
        var param = parameter.annotation(Param.class);
        if (param.isPresent()) {
            name = param.get().name().isEmpty() ? name : param.get().name();
            description = param.get().value();
        }
        name = name.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();

        List<Command.Choice> commandChoices = new ArrayList<>();
        // Options
        var choices = parameter.annotation(Choices.class);
        if (choices.isPresent()) {
            for (String option : choices.get().value()) {
                String[] parsed = option.split(":", 2);
                if (parsed.length < 1) {
                    continue;
                }
                if (parsed.length < 2) {
                    commandChoices.add(new Command.Choice(parsed[0], parsed[0]));
                    continue;
                }
                commandChoices.add(new Command.Choice(parsed[0], parsed[1]));
            }
        }
        return new ParameterDefinition(
                parameterType,
                optional.isPresent(),
                autoComplete,
                defaultValue,
                TYPE_MAPPINGS.containsKey(parameter.type()),
                name,
                description,
                commandChoices,
                constraints
        );
    }

    @Override
    public @NotNull String displayName() {
        return name;
    }

    @Override
    public @NotNull OptionData toJDAEntity() {
        OptionType optionType = OPTION_TYPE_MAPPINGS.getOrDefault(type, OptionType.STRING);

        OptionData optionData = new OptionData(
                optionType,
                name,
                description,
                !optional
        );

        optionData.addChoices(choices);
        if (optionType.canSupportChoices() && choices.isEmpty()) {
            optionData.setAutoComplete(autoComplete);
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

    public record ConstraintDefinition(Validator validator, String message,
                                       Object annotation) implements Definition {


        public static ConstraintDefinition build(@NotNull Validator validator, @NotNull Annotation annotation) {
            // annotation object is always different, so we cannot cast it. Thus, we need to get the custom error message via reflection
            var message = "";
            try {
                Method method = annotation.getClass().getDeclaredMethod("message");
                message = (String) method.invoke(annotation);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException _) {
            }
            if (message.isEmpty()) {
                message = "Parameter validation failed";
            }
            return new ConstraintDefinition(validator, message, annotation);
        }

        @Override
        public @NotNull String displayName() {
            return validator.getClass().getName();
        }
    }
}
