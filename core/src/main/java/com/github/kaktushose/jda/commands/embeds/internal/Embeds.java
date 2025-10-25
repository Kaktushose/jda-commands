package com.github.kaktushose.jda.commands.embeds.internal;

import com.github.kaktushose.jda.commands.embeds.Embed;
import com.github.kaktushose.jda.commands.embeds.EmbedConfig;
import com.github.kaktushose.jda.commands.embeds.EmbedDataSource;
import com.github.kaktushose.jda.commands.embeds.error.DefaultErrorMessageFactory;
import com.github.kaktushose.jda.commands.message.MessageResolver;
import com.github.kaktushose.jda.commands.message.placeholder.Entry;
import io.github.kaktushose.proteus.Proteus;
import io.github.kaktushose.proteus.ProteusBuilder.ConflictStrategy;
import io.github.kaktushose.proteus.type.Type;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.kaktushose.proteus.mapping.Mapper.uni;
import static io.github.kaktushose.proteus.mapping.MappingResult.lossless;

/// Container for immutably holding the embed configuration made by [EmbedConfig].
///
/// @param sources      the [EmbedDataSource]s [Embed]s can be loaded from
/// @param placeholders the global placeholders as defined in [EmbedConfig#placeholders(Map)]
@ApiStatus.Internal
public record Embeds(Collection<EmbedDataSource> sources, Map<String, Object> placeholders, MessageResolver messageResolver) {

    static {
        Proteus.global().from(Type.of(Color.class)).into(Type.of(String.class),
                uni((color, _) -> lossless(String.valueOf(color.getRGB()))),
                ConflictStrategy.OVERRIDE
        );
    }

    /// Gets an [Embed] based on the given name.
    ///
    /// @param name the name of the [Embed]
    /// @return the [Embed]
    /// @throws IllegalArgumentException if no [Embed] with the given name exists in the configured [data sources][EmbedConfig#sources(EmbedDataSource...)]
    public Embed get(String name) {
        return sources.stream()
                .map(source -> source.get(name, placeholders, messageResolver))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unknown embed " + name));
    }

    /// Gets an [Embed] based on the given name and sets the [Locale].
    ///
    /// @param name   the name of the [Embed]
    /// @param locale the [Locale] to use for localization
    /// @return the [Embed]
    /// @throws IllegalArgumentException if no [Embed] with the given name exists in the configured [data sources][EmbedConfig#sources(EmbedDataSource...)]
    public Embed get(String name, Locale locale) {
        return sources.stream()
                .map(source -> source.get(name, placeholders, messageResolver))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .peek(it -> it.locale(locale))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unknown embed " + name));
    }

    /// Checks whether an [Embed] with the given name exists in one of the [#sources()].
    ///
    /// @param name the name of the [Embed]
    /// @return `true` if the embed exists
    public boolean exists(String name) {
        return sources.stream().anyMatch(it -> it.get(name, Map.of(), messageResolver).isPresent());
    }

    public static class Configuration implements EmbedConfig {

        private final List<EmbedDataSource> sources;
        private final Map<String, Object> placeholders;
        private final MessageResolver messageResolver;
        private @Nullable EmbedDataSource errorSource;

        /// Constructs a new embed configuration builder.
        public Configuration(MessageResolver messageResolver) {
            this.messageResolver = messageResolver;
            sources = new ArrayList<>();
            placeholders = new HashMap<>();
        }

        @Override
        public Configuration placeholders(Entry... placeholders) {
            this.placeholders.putAll(Arrays.stream(placeholders).collect(Collectors.toUnmodifiableMap(Entry::name, Entry::value)));
            return this;
        }

        @Override
        public Configuration placeholders(Map<String, Object> placeholders) {
            this.placeholders.putAll(placeholders);
            return this;
        }

        @Override
        public Configuration sources(EmbedDataSource... source) {
            sources.addAll(List.of(source));
            return this;
        }

        @Override
        public Configuration errorSource(EmbedDataSource source) {
            errorSource = source;
            return this;
        }

        /// Converts this configuration into an [Embeds] instance that should be used globally.
        ///
        /// @return an [Embeds] instance for default usage
        public Embeds buildDefault() {
            return new Embeds(sources, placeholders, messageResolver);
        }

        /// Converts this configuration into an [Embeds] instance that should only be used by [DefaultErrorMessageFactory].
        ///
        /// @return an [Embeds] instance for usage inside [DefaultErrorMessageFactory]
        public Embeds buildError() {
            return new Embeds(errorSource == null ? List.of() : List.of(errorSource), placeholders, messageResolver);
        }
    }
}
