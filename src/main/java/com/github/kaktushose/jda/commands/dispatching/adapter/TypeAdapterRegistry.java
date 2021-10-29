package com.github.kaktushose.jda.commands.dispatching.adapter;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.adapter.impl.*;
import com.github.kaktushose.jda.commands.reflect.CommandDefinition;
import com.github.kaktushose.jda.commands.reflect.ParameterDefinition;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TypeAdapterRegistry {

    private static final Logger log = LoggerFactory.getLogger(TypeAdapterRegistry.class);
    private final Map<Class<?>, TypeAdapter<?>> parameterAdapters;

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
        register(TextChannel.class, new TextChannelAdapter());
        register(Role.class, new RoleAdapter());
    }

    public void register(Class<?> type, TypeAdapter<?> adapter) {
        parameterAdapters.put(type, adapter);
        log.debug("Registered adapter {} for type {}", adapter.getClass().getName(), type.getName());
    }

    public void unregister(Class<?> type) {
        parameterAdapters.remove(type);
        log.debug("Unregistered adapter for type {}", type.getName());
    }

    public boolean exists(Class<?> type) {
        return parameterAdapters.containsKey(type);
    }

    public Optional<TypeAdapter<?>> get(Class<?> type) {
        return Optional.ofNullable(parameterAdapters.get(type));
    }

    public void adapt(CommandContext context) {
        CommandDefinition command = context.getCommand();
        List<Object> arguments = new ArrayList<>();
        String[] input = context.getInput();

        log.debug("Type adapting arguments...");
        MessageReceivedEvent event = context.getEvent();
        arguments.add(new CommandEvent(event.getJDA(), event.getResponseNumber(), event.getMessage(), command, context));
        // start with index 1 so we skip the CommandEvent
        for (int i = 1; i < command.getParameters().size(); i++) {
            ParameterDefinition parameter = command.getParameters().get(i);

            // if parameter is array don't parse
            log.debug("First parameter is String array. Not adapting arguments");
            if (String[].class.isAssignableFrom(parameter.getType())) {
                arguments.add(input);
                break;
            }

            String raw;
            // current parameter index > total amount of input, check if it's optional else cancel context
            if (i > input.length) {
                if (!parameter.isOptional()) {
                    context.setCancelled(true);
                    context.setErrorMessage(new MessageBuilder().append("argument mismatch").build());
                    break;
                }

                // if the default value is an empty String (thus not present) add a null value to the argument list
                // else try to type adapt the default value
                if (parameter.getDefaultValue() == null) {
                    arguments.add(null);
                    continue;
                } else {
                    raw = parameter.getDefaultValue();
                }
            } else {
                // - 1 because we start with index 1
                raw = input[i - 1];
            }

            log.debug("Trying to adapt input \"{}\" to type {}", raw, parameter.getType().getName());

            Optional<TypeAdapter<?>> adapter = get(parameter.getType());
            if (!adapter.isPresent()) {
                throw new IllegalArgumentException("No type adapter found!");
            }

            Optional<?> parsed = adapter.get().parse(raw, context);
            if (!parsed.isPresent()) {
                log.debug("Type adapting failed!");
                context.setCancelled(true);
                context.setErrorMessage(new MessageBuilder().append("argument mismatch").build());
                break;
            }

            arguments.add(parsed.get());
            log.debug("Added {} to the argument list", parsed.get());
        }
        context.setArguments(arguments);
    }
}
