package com.github.kaktushose.jda.commands.embeds.internal;

import com.github.kaktushose.jda.commands.embeds.Embed;
import com.github.kaktushose.jda.commands.embeds.EmbedConfig;
import com.github.kaktushose.jda.commands.embeds.EmbedDataSource;
import com.github.kaktushose.jda.commands.embeds.error.DefaultErrorMessageFactory;
import com.github.kaktushose.jda.commands.i18n.I18n;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/// Container for immutably holding the embed configuration made by [EmbedConfig].
///
/// @param sources the [EmbedDataSource]s [Embed]s can be loaded from
/// @param placeholders the global placeholders as defined in [EmbedConfig#placeholders(Map)]
@ApiStatus.Internal
public record Embeds(@NotNull Collection<EmbedDataSource> sources, @NotNull Map<String, Object> placeholders, @NotNull I18n i18n) {

    /// Gets an [Embed] based on the given name.
    ///
    /// @param name the name of the [Embed]
    /// @return the [Embed]
    /// @throws IllegalArgumentException if no [Embed] with the given name exists in the configured [data sources][EmbedConfig#sources(EmbedDataSource)]
    @NotNull
    public Embed get(@NotNull String name) {
        return sources.stream()
                .map(source -> source.get(name, placeholders, Objects.requireNonNull(i18n)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unknown embed " + name));
    }

    /// Gets an [Embed] based on the given name and sets the [Locale].
    ///
    /// @param name the name of the [Embed]
    /// @param locale the [Locale] to use for localization
    /// @return the [Embed]
    /// @throws IllegalArgumentException if no [Embed] with the given name exists in the configured [data sources][EmbedConfig#sources(EmbedDataSource)]
    @NotNull
    public Embed get(@NotNull String name, @NotNull Locale locale) {
        return sources.stream()
                .map(source -> source.get(name, placeholders, Objects.requireNonNull(i18n)))
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
        return sources.stream()
                .map(source -> source.get(name, Map.of(), Objects.requireNonNull(i18n)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny()
                .isPresent();
    }

    public static class Configuration implements EmbedConfig {

        private final List<EmbedDataSource> sources;
        private final Map<String, Object> placeholders;
        private final I18n i18n;
        private EmbedDataSource errorSource;

        /// Constructs a new embed configuration builder.
        public Configuration(I18n i18n) {
            this.i18n = i18n;
            sources = new ArrayList<>();
            placeholders = new HashMap<>();
        }

        @NotNull
        @Override
        public Configuration placeholders(@NotNull I18n.Entry... placeholders) {
            this.placeholders.putAll(Arrays.stream(placeholders).collect(Collectors.toUnmodifiableMap(I18n.Entry::name, I18n.Entry::value)));
            return this;
        }

        @NotNull
        @Override
        public Configuration placeholders(@NotNull Map<String, Object> placeholders) {
            this.placeholders.putAll(placeholders);
            return this;
        }

        @NotNull
        @Override
        public Configuration sources(@NotNull EmbedDataSource... source) {
            sources.addAll(List.of(source));
            return this;
        }

        @NotNull
        @Override
        public Configuration errorSource(@NotNull EmbedDataSource source) {
            errorSource = source;
            return this;
        }

        /// Converts this configuration into an [Embeds] instance that should be used globally.
        ///
        /// @return an [Embeds] instance for default usage
        @NotNull
        public Embeds buildDefault() {
            return new Embeds(sources, placeholders, i18n);
        }

        /// Converts this configuration into an [Embeds] instance that should only be used by [DefaultErrorMessageFactory].
        ///
        /// @return an [Embeds] instance for usage inside of [DefaultErrorMessageFactory]
        @NotNull
        public Embeds buildError() {
            return new Embeds(List.of(errorSource), Map.of(), i18n);
        }
    }
}
