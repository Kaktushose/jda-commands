package com.github.kaktushose.jda.commands.definitions.interactions.command;

import com.github.kaktushose.jda.commands.annotations.constraints.Constraint;
import com.github.kaktushose.jda.commands.annotations.constraints.Max;
import com.github.kaktushose.jda.commands.annotations.constraints.Min;
import com.github.kaktushose.jda.commands.annotations.interactions.Choices;
import com.github.kaktushose.jda.commands.annotations.interactions.Param;
import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.description.ParameterDescription;
import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.interactions.AutoCompleteDefinition;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.dispatching.validation.internal.Validators;
import io.github.kaktushose.proteus.Proteus;
import io.github.kaktushose.proteus.type.Type;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static java.util.Map.entry;

/// Representation of a slash command option.
///
/// @param declaredType the [Class] declaredType of the command option
/// @param resolvedType the type after wrapping primitive types and unwrapping [Optional]
/// @param optionType   the [OptionType] of the command option
/// @param optional     whether this command option is optional
/// @param autoComplete he [AutoCompleteDefinition] for this option or `null` if no auto complete was defined
/// @param name         the name of the command option
/// @param description  the description of the command option
/// @param choices      a [SequencedCollection] of possible [Command.Choice]s for this command option
/// @param constraints  a [Collection] of [ConstraintDefinition]s of this command option
public record OptionDataDefinition(
        @NotNull Class<?> declaredType,
        @NotNull Class<?> resolvedType,
        @NotNull OptionType optionType,
        boolean optional,
        @Nullable AutoCompleteDefinition autoComplete,
        @NotNull String name,
        @NotNull String description,
        @NotNull SequencedCollection<Command.Choice> choices,
        @NotNull Collection<ConstraintDefinition> constraints
) implements Definition, JDAEntity<OptionData> {


    private static final Map<OptionType, Class<?>> OPTION_TYPE_TO_CLASS = Map.ofEntries(
            entry(OptionType.STRING, String.class),
            entry(OptionType.BOOLEAN, Boolean.class),
            entry(OptionType.INTEGER, Long.class),
            entry(OptionType.NUMBER, Double.class),
            entry(OptionType.USER, User.class),
            entry(OptionType.ROLE, Role.class),
            entry(OptionType.MENTIONABLE, IMentionable.class),
            entry(OptionType.CHANNEL, GuildChannelUnion.class),
            entry(OptionType.ATTACHMENT, Message.Attachment.class)
    );

    private static final Map<Class<?>, OptionType> CLASS_TO_OPTION_TYPE = Map.ofEntries(
            entry(Boolean.class, OptionType.BOOLEAN),
            entry(Short.class, OptionType.INTEGER),
            entry(Integer.class, OptionType.INTEGER),
            entry(Long.class, OptionType.INTEGER),
            entry(Float.class, OptionType.NUMBER),
            entry(Double.class, OptionType.NUMBER),
            entry(User.class, OptionType.USER),
            entry(Member.class, OptionType.USER),
            entry(Role.class, OptionType.ROLE),
            entry(IMentionable.class, OptionType.MENTIONABLE),
            entry(GuildChannelUnion.class, OptionType.CHANNEL),
            entry(GuildChannel.class, OptionType.CHANNEL),
            entry(AudioChannel.class, OptionType.CHANNEL),
            entry(GuildMessageChannel.class, OptionType.CHANNEL),
            entry(NewsChannel.class, OptionType.CHANNEL),
            entry(StageChannel.class, OptionType.CHANNEL),
            entry(TextChannel.class, OptionType.CHANNEL),
            entry(ThreadChannel.class, OptionType.CHANNEL),
            entry(VoiceChannel.class, OptionType.CHANNEL),
            entry(Message.Attachment.class, OptionType.ATTACHMENT)
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

    /// Builds a new [OptionDataDefinition].
    ///
    /// @param parameter         the [ParameterDescription] to build the [OptionDataDefinition] from
    /// @param autoComplete      the [AutoCompleteDefinition] for this option or `null` if no auto complete was defined
    /// @param validatorRegistry the corresponding [Validators]
    /// @return the [OptionDataDefinition]
    @NotNull
    public static OptionDataDefinition build(@NotNull ParameterDescription parameter,
                                             @Nullable AutoCompleteDefinition autoComplete,
                                             @NotNull Validators validatorRegistry) {
        Class<?> resolvedType = resolveType(parameter.type(), parameter);

        // index constraints
        List<ConstraintDefinition> constraints = new ArrayList<>();
        parameter.annotations().stream()
                .filter(it -> it.annotationType().isAnnotationPresent(Constraint.class))
                .filter(it -> !(it.annotationType().isAssignableFrom(Min.class) || it.annotationType().isAssignableFrom(Max.class)))
                .forEach(it -> {
                    var validator = validatorRegistry.get(it.annotationType(), resolvedType)
                            .orElseThrow(() -> new IllegalStateException("No validator found for %s on %s".formatted(it, parameter)));
                    constraints.add(ConstraintDefinition.build(validator, it));
                });

        // Param
        String name = parameter.name();
        String description = "empty description";
        boolean isOptional = false;
        OptionType optionType = CLASS_TO_OPTION_TYPE.getOrDefault(resolvedType, OptionType.STRING);
        var param = parameter.annotation(Param.class);
        if (param.isPresent()) {
            Param annotation = param.get();
            name = annotation.name().isEmpty() ? name : annotation.name();
            description = annotation.value().isEmpty() ? description : annotation.value();
            isOptional = annotation.optional() | parameter.type().equals(Optional.class);
            if (annotation.type() != OptionType.UNKNOWN) {
                optionType = annotation.type();
            }
        }
        name = name.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();

        // Options
        List<Command.Choice> commandChoices = new ArrayList<>();
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

        return new OptionDataDefinition(
                parameter.type(),
                resolvedType,
                optionType,
                isOptional,
                autoComplete,
                name,
                description,
                commandChoices,
                constraints
        );
    }

    private static Class<?> resolveType(Class<?> type, ParameterDescription description) {
        if (type.equals(Optional.class)) {
            Class<?> unwrapped = description.typeArguments()[0];
            if (unwrapped == null) {
                throw new IllegalArgumentException("Generic parameter of Optional cannot be parsed to class. Please provide a valid generic type and don't use any wildcard!");
            }
            return unwrapped;
        }

        return MethodType.methodType(type).wrap().returnType();
    }

    @NotNull
    @Override
    public String displayName() {
        return name;
    }

    /// Transforms this definition into [OptionData].
    ///
    /// @return the [OptionData]
    @NotNull
    @Override
    public OptionData toJDAEntity() {
        if (!declaredType.equals(Optional.class) && !Proteus.global().existsPath(Type.of(OPTION_TYPE_TO_CLASS.get(optionType)), Type.of(declaredType))) {
            throw new IllegalStateException(
                    "Cannot create option data! " +
                    "There is no type adapting path to convert from OptionType '%s' (underlying type: '%s') to '%s'. "
                            .formatted(optionType, OPTION_TYPE_TO_CLASS.get(optionType).getName(), declaredType.getName()) +
                    "Please add a respective TypeAdapter ('%s' => '%s') or change the OptionType."
                            .formatted(OPTION_TYPE_TO_CLASS.get(optionType).getName(), declaredType.getName())
            );
        }

        OptionData optionData = new OptionData(
                optionType,
                name,
                description,
                !optional
        );

        optionData.addChoices(choices);
        if (optionType.canSupportChoices() && choices.isEmpty()) {
            optionData.setAutoComplete(autoComplete != null);
        }

        constraints.stream().filter(constraint ->
                constraint.annotation() instanceof Min
        ).findFirst().ifPresent(constraint -> optionData.setMinValue(((Min) constraint.annotation()).value()));

        constraints.stream().filter(constraint ->
                constraint.annotation() instanceof Max
        ).findFirst().ifPresent(constraint -> optionData.setMaxValue(((Max) constraint.annotation()).value()));

        java.util.Optional.ofNullable(CHANNEL_TYPE_RESTRICTIONS.get(declaredType)).ifPresent(optionData::setChannelTypes);

        return optionData;
    }

    /// Representation of a parameter constraint defined by a constraint annotation.
    ///
    /// @param validator  the corresponding [Validator]
    /// @param message    the message to display if the constraint fails
    /// @param annotation the corresponding annotation object
    public record ConstraintDefinition(Validator validator, String message, Object annotation) implements Definition {

        /// Builds a new  [ConstraintDefinition].
        ///
        /// @param validator  the corresponding [Validator]
        /// @param annotation the corresponding annotation object
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

        @NotNull
        @Override
        public String displayName() {
            return validator.getClass().getName();
        }
    }
}
