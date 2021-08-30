package com.github.kaktushose.jda.commands.rewrite.adapters;

import com.github.kaktushose.jda.commands.entities.Parameter;
import com.github.kaktushose.jda.commands.rewrite.adapters.impl.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ParameterAdapterRegistry {

    private final Map<Class<?>, ParameterAdapter<?>> parameterAdapters;


    public ParameterAdapterRegistry() {
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
        register(String.class, (ParameterAdapter<String>) (raw, guild) -> Optional.of(raw));
        register(String[].class, (ParameterAdapter<String>) (raw, guild) -> Optional.of(raw));

        // jda specific
        register(Member.class, new MemberAdapter());
        register(User.class, new UserAdapter());
        register(TextChannel.class, new TextChannelAdapter());
        register(Role.class, new RoleAdapter());

    }

    public void register(Class<?> type, ParameterAdapter<?> adapter) {
        parameterAdapters.put(type, adapter);
    }

    public void unregister(Class<?> type) {
        parameterAdapters.remove(type);
    }

    public Optional<ParameterAdapter<?>> get(Class<?> type) {
        return Optional.ofNullable(parameterAdapters.get(type));
    }

    public boolean exists(Class<?> type) {
        return parameterAdapters.containsKey(type);
    }

}
