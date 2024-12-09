package com.github.kaktushose.jda.commands.dispatching.adapter;

import com.github.kaktushose.jda.commands.dispatching.adapter.impl.*;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.SlashCommandContext;
import com.github.kaktushose.jda.commands.dispatching.refactor.ExecutionContext;
import com.github.kaktushose.jda.commands.embeds.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.reflect.ParameterDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.SlashCommandDefinition;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Central registry for all type adapters.
 *
 * @see TypeAdapter
 * @since 2.0.0
 */
public class TypeAdapterRegistry {

    private static final Logger log = LoggerFactory.getLogger(TypeAdapterRegistry.class);
    private static final Map<Class<?>, Object> DEFAULT_MAPPINGS = new HashMap<>() {
        {
            put(byte.class, (byte) 0);
            put(short.class, (short) 0);
            put(int.class, 0);
            put(long.class, 0L);
            put(double.class, 0.0d);
            put(float.class, 0.0f);
            put(boolean.class, false);
            put(char.class, '\u0000');
        }
    };
    private final Map<Class<?>, TypeAdapter<?>> parameterAdapters;

    /**
     * Constructs a new TypeAdapterRegistry. This will register default type adapters for:
     * <ul>
     *     <li>all primitive data types</li>
     *     <li>String</li>
     *     <li>String Array</li>
     *     <li>{@link Member}</li>
     *     <li>{@link User}</li>
     *     <li>{@link TextChannel}</li>
     *     <li>{@link Role}</li>
     * </ul>
     */
    public TypeAdapterRegistry() {
        parameterAdapters = new HashMap<>();

        // default types
        register(Byte.class, new ByteAdapter());
        register(Short.class, new ShortAdapter());
        register(Integer.class, new IntegerAdapter());
        register(Long.class, new LongAdapter());
        register(Float.class, new FloatAdapter());
        register(Double.class, new DoubleAdapter());
        register(Character.class, new CharacterAdapter());
        register(Boolean.class, new BooleanAdapter());
        register(String.class, (TypeAdapter<String>) (raw, guild) -> Optional.of(raw));
        register(String[].class, (TypeAdapter<String>) (raw, guild) -> Optional.of(raw));

        // jda specific
        register(Member.class, new MemberAdapter());
        register(User.class, new UserAdapter());
        register(GuildChannel.class, new GuildChannelAdapter());
        register(GuildMessageChannel.class, new GuildMessageChannelAdapter());
        register(ThreadChannel.class, new ThreadChannelAdapter());
        register(TextChannel.class, new TextChannelAdapter());
        register(NewsChannel.class, new NewsChannelAdapter());
        register(AudioChannel.class, new AudioChannelAdapter());
        register(VoiceChannel.class, new VoiceChannelAdapter());
        register(StageChannel.class, new StageChannelAdapter());
        register(Role.class, new RoleAdapter());
    }

    /**
     * Registers a new type adapter.
     *
     * @param type    the type the adapter is for
     * @param adapter the {@link TypeAdapter}
     */
    public void register(@NotNull Class<?> type, @NotNull TypeAdapter<?> adapter) {
        parameterAdapters.put(type, adapter);
        log.debug("Registered adapter {} for type {}", adapter.getClass().getName(), type.getName());
    }

    /**
     * Unregisters a new type adapter.
     *
     * @param type the type the adapter is for
     */
    public void unregister(@NotNull Class<?> type) {
        parameterAdapters.remove(type);
        log.debug("Unregistered adapter for type {}", type.getName());
    }

    /**
     * Checks if a type adapter for the given type exists.
     *
     * @param type the type to check
     * @return {@code true} if a type adapter exists
     */
    public boolean exists(@Nullable Class<?> type) {
        return parameterAdapters.containsKey(type);
    }

    /**
     * Retrieves a type adapter.
     *
     * @param type the type to get the adapter for
     * @return the type adapter or an empty Optional if none found
     */
    public Optional<TypeAdapter<?>> get(@Nullable Class<?> type) {
        return Optional.ofNullable(parameterAdapters.get(type));
    }

    /**
     * Takes a {@link SlashCommandContext} and attempts to type adapt the command input to the type specified by the
     * {@link SlashCommandDefinition}. Cancels the {@link SlashCommandContext} if the type adapting fails.
     *
     * @param context the {@link SlashCommandContext} to type adapt
     */
    public List<Object> adapt(ExecutionContext<SlashCommandInteractionEvent, SlashCommandDefinition> context) {
        SlashCommandDefinition command = context.interactionDefinition();

        var input = command.getActualParameters().stream()
                .map(it -> context.event().getOption(it.name()).getAsString())
                .toArray(String[]::new);

        List<Object> arguments = new ArrayList<>();
        ErrorMessageFactory messageFactory = context.implementationRegistry().getErrorMessageFactory();

        log.debug("Type adapting arguments...");
        for (int i = 0; i < command.getActualParameters().size(); i++) {
            ParameterDefinition parameter = command.getActualParameters().get(i);

            // if parameter is array don't parse
            if (String[].class.isAssignableFrom(parameter.type())) {
                log.debug("First parameter is String array. Not adapting arguments");
                arguments.add(input);
                break;
            }

            String raw;
            // current parameter index == total amount of input, check if it's optional else cancel context
            if (i >= input.length) {
                if (!parameter.isOptional()) {
                    IllegalStateException exception = new IllegalStateException(
                            "Command input doesn't match parameter length! Please report this error the the devs of jda-commands."
                    );
                    context.cancel(messageFactory.getCommandExecutionFailedMessage(context, exception));
                    throw exception;
                }

                // if the default value is an empty String (thus not present) add a null value to the argument list
                // else try to type adapt the default value
                if (parameter.defaultValue() == null) {
                    arguments.add(DEFAULT_MAPPINGS.getOrDefault(parameter.type(), null));
                    continue;
                } else {
                    raw = parameter.defaultValue();
                }
            } else {
                raw = input[i];
            }

            log.debug("Trying to adapt input \"{}\" to type {}", raw, parameter.type().getName());

            Optional<TypeAdapter<?>> adapter = get(parameter.type());
            if (adapter.isEmpty()) {
                throw new IllegalArgumentException("No type adapter found!");
            }

            Optional<?> parsed = adapter.get().apply(raw, context);
            if (parsed.isEmpty()) {
                log.debug("Type adapting failed!");
                context.cancel(messageFactory.getTypeAdaptingFailedMessage(context));
                break;
            }

            arguments.add(parsed.get());
            log.debug("Added \"{}\" to the argument list", parsed.get());
        }
        return arguments;
    }
}
