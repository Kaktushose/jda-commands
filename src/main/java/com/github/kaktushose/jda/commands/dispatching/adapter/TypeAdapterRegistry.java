package com.github.kaktushose.jda.commands.dispatching.adapter;

import com.github.kaktushose.jda.commands.dispatching.adapter.impl.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Central registry for all type adapters.
 *
 * @see TypeAdapter
 * @since 2.0.0
 */
public class TypeAdapterRegistry {

    private static final Logger log = LoggerFactory.getLogger(TypeAdapterRegistry.class);
    public static final Map<Class<?>, Object> DEFAULT_MAPPINGS = new HashMap<>() {
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
        register(String.class, (TypeAdapter<String>) (raw, _) -> Optional.of(raw));
        register(String[].class, (TypeAdapter<String>) (raw, _) -> Optional.of(raw));

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


}
