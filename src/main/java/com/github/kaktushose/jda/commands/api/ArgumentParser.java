package com.github.kaktushose.jda.commands.api;

import com.github.kaktushose.jda.commands.entities.CommandCallable;
import com.github.kaktushose.jda.commands.entities.CommandEvent;
import com.github.kaktushose.jda.commands.entities.JDACommands;
import com.github.kaktushose.jda.commands.entities.Parameter;
import com.github.kaktushose.jda.commands.internal.ParameterType;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The default argument parser of this framework.
 *
 * <p> This argument parser supports all primitive data types as well as the following JDA entities: Member, User, TextChannel and Role.
 *
 * @author Kaktushose
 * @version 1.0.0
 * @since 1.0.0
 */
public class ArgumentParser {

    /**
     * Attempts to parse a List of Strings or respectively the user input in a way to match the parameters of a command.
     *
     * <p>Therefore it is attempted to cast each element of the array to the type described by the corresponding {@link Parameter}.
     * If the argument parsing fails for one argument and if the {@link Parameter} isn't optional, then
     * the argument parsing stops and an empty Optional gets returned.
     *
     * @param commandCallable the Command that was mapped
     * @param event           the corresponding {@code GuildMessageReceivedEvent}
     * @param rawArguments    the List of Strings to be parsed
     * @param jdaCommands     the {@link JDACommands} object
     * @return an optional containing a list of all casted objects or an empty optional if the argument parsing failed
     * @see com.github.kaktushose.jda.commands.annotations.Optional
     */
    public Optional<List<Object>> parseArguments(@Nonnull CommandCallable commandCallable,
                                                 @Nonnull GuildMessageReceivedEvent event,
                                                 @Nonnull List<String> rawArguments,
                                                 @Nonnull JDACommands jdaCommands) {
        List<Parameter> parameters = commandCallable.getParameters();
        List<Object> parsedArguments = new ArrayList<>();
        parsedArguments.add(new CommandEvent(event.getJDA(), event.getResponseNumber(), event.getMessage(), commandCallable, jdaCommands));

        for (int i = 0; i < parameters.size(); i++) {
            Parameter parameter = parameters.get(i);

            // if parameter is array don't parse
            if (parameter.getParameterType().equals(ParameterType.ARRAY)) {
                parsedArguments.add(rawArguments.toArray());
                return Optional.of(parsedArguments);
            }

            // check if argument size is matching
            if (i >= rawArguments.size()) {
                if (parameter.isOptional()) {
                    if (parameter.getDefaultValue().isEmpty()) {
                        parsedArguments.add(null);
                    } else {
                        parsedArguments.add(parse(parameter.getDefaultValue(), parameter, event.getGuild()).orElse(null));
                    }
                    continue;
                } else {
                    return Optional.empty();
                }
            }

            // actual argument parsing
            Optional<?> optional = parse(rawArguments.get(i), parameter, event.getGuild());
            if (!optional.isPresent()) {
                return Optional.empty();
            }

            // check for concat parameter
            if (i == parameters.size() - 1 && parameter.isConcat()) {
                StringBuilder sb = new StringBuilder();
                for (String s : rawArguments.subList(i, rawArguments.size())) {
                    sb.append(s).append(" ");
                }
                parsedArguments.add(sb.toString().trim());
                break;
            }
            parsedArguments.add(optional.get());
        }
        return Optional.of(parsedArguments);
    }

    /**
     * Attempts to cast a String to an object that matches the passed {@link Parameter}. In order to parse JDA entities a
     * {@code Guild} object is also required.
     *
     * @param argument  the argument to parse
     * @param parameter the parameter to match
     * @param guild     the {@code Guild} for possible JDA entities
     * @return an Optional describing the parsed argument or an empty Optional if the argument parsing failed
     */
    protected Optional<?> parse(@Nonnull String argument, @Nonnull Parameter parameter, @Nonnull Guild guild) {
        String typeName = parameter.getParameterType().name;
        if (ParameterType.isJDAEntity(typeName)) {
            return parseJDAEntity(argument, typeName, guild);
        }
        return parseByStringConstructor(argument, typeName);
    }

    /**
     * Attempts to cast a String to an object. The object to match is described by its class name and has to have a String constructor.
     * If no constructor is available or the casting fails an empty Optional gets returned
     *
     * @param argument  the argument to parse
     * @param parameter the class name to match
     * @return an Optional describing the parsed argument or an empty Optional if the argument parsing failed
     */
    protected Optional<?> parseByStringConstructor(@Nonnull String argument, @Nonnull String parameter) {
        try {
            Class<?> clazz = Class.forName(parameter);
            return Optional.of(clazz.getConstructor(String.class).newInstance(argument));
        } catch (Exception ignore) {
            return Optional.empty();
        }
    }

    /**
     * Attempts to cast a String to an JDA entity.
     *
     * @param argument  the argument to parse
     * @param parameter the class name of the jda entity to match
     * @param guild     the {@code Guild} to cast JDA entities
     * @return an Optional describing the parsed JDA entity or an empty Optional if the argument parsing failed
     */
    protected Optional<?> parseJDAEntity(@Nonnull String argument, @Nonnull String parameter, @Nonnull Guild guild) {
        ParameterType type = ParameterType.getByName(parameter);
        argument = formatString(argument);
        switch (type) {
            case MEMBER:
                return parseMember(argument, guild);
            case USER:
                return parseUser(argument, guild);
            case ROLE:
                return parseRole(argument, guild);
            case TEXTCHANNEL:
                return parseChannel(argument, guild);
            default:
                return Optional.empty();
        }
    }

    /**
     * Attempts to cast a String to a {@code Member}.
     *
     * @param argument the argument to parse.
     * @param guild    the {@code Guild} needed for casting
     * @return an Optional describing the {@code Member} or an empty Optional if the argument parsing failed
     */
    protected Optional<Member> parseMember(@Nonnull String argument, @Nonnull Guild guild) {
        Member member;
        if (argument.matches("\\d+")) {
            member = guild.getMemberById(argument);
        } else {
            member = guild.getMembersByName(argument, true).stream().findFirst().orElse(null);
        }
        if (member == null) {
            return Optional.empty();
        }
        return Optional.of(member);
    }

    /**
     * Attempts to cast a String to a {@code User}.
     *
     * @param argument the argument to parse.
     * @param guild    the {@code Guild} needed for casting
     * @return an Optional describing the {@code User} or an empty Optional if the argument parsing failed
     */
    protected Optional<User> parseUser(@Nonnull String argument, @Nonnull Guild guild) {
        User user;
        if (argument.matches("\\d+")) {
            user = guild.getJDA().getUserById(argument);
        } else {
            user = guild.getJDA().getUsersByName(argument, true).stream().findFirst().orElse(null);
        }
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    /**
     * Attempts to cast a String to a {@code TextChannel}.
     *
     * @param argument the argument to parse.
     * @param guild    the {@code Guild} needed for casting
     * @return an Optional describing the {@code TextChannel} or an empty Optional if the argument parsing failed
     */
    protected Optional<TextChannel> parseChannel(@Nonnull String argument, @Nonnull Guild guild) {
        TextChannel textChannel;
        if (argument.matches("\\d+")) {
            textChannel = guild.getTextChannelById(argument);
        } else {
            textChannel = guild.getTextChannelsByName(argument, true).stream().findFirst().orElse(null);
        }
        if (textChannel == null) {
            return Optional.empty();
        }
        return Optional.of(textChannel);
    }

    /**
     * Attempts to cast a String to a Role.
     *
     * @param argument the argument to parse.
     * @param guild    the {@code Guild} needed for casting
     * @return an Optional describing the Role or an empty Optional if the argument parsing failed
     */
    protected Optional<Role> parseRole(@Nonnull String argument, @Nonnull Guild guild) {
        Role role;
        if (argument.matches("\\d+")) {
            role = guild.getRoleById(argument);
        } else {
            role = guild.getRolesByName(argument, true).stream().findFirst().orElse(null);
        }
        if (role == null) {
            return Optional.empty();
        }
        return Optional.of(role);
    }

    /**
     * Strips of all characters used in a mention like {@code @, #, &, !, <, >} from a String.
     * If the String doesn't represent a mention, it gets returned without being manipulated.
     *
     * @param mention The String to be stripped of mention characters
     * @return The String with all mention characters removed
     */
    protected String formatString(String mention) {
        if (mention.matches("<[@#][&!]?([0-9]{4,})>")) {
            return mention.replaceAll("<[@#][&!]?", "").replace(">", "");
        }
        return mention;
    }

}