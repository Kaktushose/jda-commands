package com.github.kaktushose.jda.commands.dispatching.adapter.internal;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.adapter.impl.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/// Central registry for all type adapters.
///
/// @see TypeAdapter
public class TypeAdapters {

    public static final Map<Class<?>, Object> DEFAULT_MAPPINGS = Map.of(
            byte.class, ((byte) 0),
            short.class, ((short) 0),
            int.class, 0,
            long.class, 0L,
            double.class, 0.0d,
            float.class, 0.0f,
            boolean.class, false,
            char.class, '\u0000'
    );
    private final Map<Class<?>, TypeAdapter<?>> parameterAdapters;

    /// Constructs a new TypeAdapters. This will register default type adapters for:
    ///
    ///   - all primitive data types
    ///   - [String]
    ///   - [String] Array
    ///   - [Member]
    ///   - [User]
    ///   - [MessageChannel] and subtypes
    ///   - [Role]
    public TypeAdapters(Map<Class<?>, TypeAdapter<?>> parameterAdapters) {
        HashMap<Class<?>, TypeAdapter<?>> adapterMap = new HashMap<>(parameterAdapters);

        // default types
        adapterMap.put(Byte.class, new ByteAdapter());
        adapterMap.put(Short.class, new ShortAdapter());
        adapterMap.put(Integer.class, new IntegerAdapter());
        adapterMap.put(Long.class, new LongAdapter());
        adapterMap.put(Float.class, new FloatAdapter());
        adapterMap.put(Double.class, new DoubleAdapter());
        adapterMap.put(Character.class, new CharacterAdapter());
        adapterMap.put(Boolean.class, new BooleanAdapter());
        adapterMap.put(String.class, (TypeAdapter<String>) (raw, _) -> Optional.of(raw));
        adapterMap.put(String[].class, (TypeAdapter<String>) (raw, _) -> Optional.of(raw));

        // jda specific
        adapterMap.put(Member.class, new MemberAdapter());
        adapterMap.put(User.class, new UserAdapter());
        adapterMap.put(GuildChannel.class, new GuildChannelAdapter());
        adapterMap.put(GuildMessageChannel.class, new GuildMessageChannelAdapter());
        adapterMap.put(ThreadChannel.class, new ThreadChannelAdapter());
        adapterMap.put(TextChannel.class, new TextChannelAdapter());
        adapterMap.put(NewsChannel.class, new NewsChannelAdapter());
        adapterMap.put(AudioChannel.class, new AudioChannelAdapter());
        adapterMap.put(VoiceChannel.class, new VoiceChannelAdapter());
        adapterMap.put(StageChannel.class, new StageChannelAdapter());
        adapterMap.put(Role.class, new RoleAdapter());

        this.parameterAdapters = Collections.unmodifiableMap(adapterMap);
    }

    /// Checks if a type adapter for the given type exists.
    ///
    /// @param type the type to check
    /// @return `true` if a type adapter exists
    public boolean exists(@Nullable Class<?> type) {
        return parameterAdapters.containsKey(type);
    }

    /// Retrieves a type adapter.
    ///
    /// @param type the type to get the adapter for
    /// @return the type adapter or an empty Optional if none found
    public Optional<TypeAdapter<?>> get(@Nullable Class<?> type) {
        return Optional.ofNullable(parameterAdapters.get(type));
    }


}
