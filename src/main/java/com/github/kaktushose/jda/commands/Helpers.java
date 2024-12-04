package com.github.kaktushose.jda.commands;

import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.reflect.MethodBuildContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class Helpers {

    private static final Logger log = LoggerFactory.getLogger(com.github.kaktushose.jda.commands.Helpers.class);

    /**
     * Sanitizes a String containing a raw mention. This will remove all markdown characters namely <em>< @ # & ! ></em>
     * For instance: {@code <@!393843637437464588>} gets sanitized to {@code 393843637437464588}
     *
     * @param mention the raw String to sanitize
     * @return the sanitized String
     */
    public static String sanitizeMention(@NotNull String mention) {
        if (mention.matches("<[@#][&!]?([0-9]{4,})>")) {
            return mention.replaceAll("<[@#][&!]?", "").replace(">", "");
        }
        return mention;
    }

    public static Optional<GuildChannel> resolveGuildChannel(Context context, String raw) {
        GuildChannel guildChannel;
        raw = sanitizeMention(raw);

        Guild guild = context.getEvent().getGuild();
        if (raw.matches("\\d+")) {
            guildChannel = guild.getGuildChannelById(raw);
        } else {
            String finalRaw = raw;
            guildChannel = guild.getChannels().stream().filter(it -> it.getName().equalsIgnoreCase(finalRaw))
                    .findFirst().orElse(null);
        }
        return Optional.ofNullable(guildChannel);
    }

    public static Set<String> permissions(MethodBuildContext context) {
        Permissions permission = context.method().getAnnotation(Permissions.class);

        if (permission != null) {
            HashSet<String> mergedPermissions = new HashSet<>(context.permissions());
            mergedPermissions.addAll(Set.of(permission.value()));
            return Collections.unmodifiableSet(mergedPermissions);
        }
        return context.permissions();
    }

    public static boolean ephemeral(MethodBuildContext context, boolean localEphemeral) {
        return context.interaction().ephemeral() || localEphemeral;
    }

    public static boolean isIncorrectParameterType(Method method, int index, Class<?> type) {
        if (!type.isAssignableFrom(method.getParameters()[index].getType())) {
            log.error("An error has occurred! Skipping Interaction {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException(String.format("%d. parameter must be of type %s", index+1, type.getSimpleName())));
            return true;
        }
        return false;
    }

    public static boolean isIncorrectParameterAmount(Method method, int amount) {
        if (method.getParameters().length != amount) {
            log.error("An error has occurred! Skipping Interaction {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException(String.format(
                            "Invalid amount of parameters!. Expected: %d Actual: %d",
                            amount,
                            method.getParameters().length
                    )));
            return true;
        }
        return false;
    }
}
