package com.github.kaktushose.jda.commands.embeds;

import com.github.kaktushose.jda.commands.embeds.Embed.Placeholder;
import com.github.kaktushose.jda.commands.i18n.I18n;
import com.github.kaktushose.jda.commands.i18n.Localizer;
import dev.goldmensch.fluava.Fluava;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

/// Container for immutably holding the embed configuration made by [Configuration].
///
/// @param sources the [EmbedDataSource]s [Embed]s can be loaded from
/// @param placeholders the global [Placeholder]s
public record Embeds(@NotNull Collection<EmbedDataSource> sources, @NotNull Collection<Placeholder> placeholders, @Nullable I18n i18n) {

    public Embeds {
        sources = Collections.unmodifiableCollection(sources);
        placeholders = Collections.unmodifiableCollection(placeholders);

        if (i18n == null && !sources.isEmpty()) {
            throw new IllegalStateException("I18n instance cannot be null if EmbedDataSources are present!");
        }
    }

    /// Constructs an empty [Embeds] container with no [EmbedDataSource]s or [Placeholder]s registered.
    @NotNull
    @ApiStatus.Internal
    public static Embeds empty() {
        return new Embeds(Collections.emptyList(), Collections.emptyList(), null);
    }

    /// Gets an [Embed] based on the given name.
    ///
    /// @param name the name of the [Embed]
    /// @return the [Embed]
    /// @throws IllegalArgumentException if no [Embed] with the given name exists in the configured [data sources][Embeds.Configuration#source(EmbedDataSource)]
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
    /// @throws IllegalArgumentException if no [Embed] with the given name exists in the configured [data sources][Embeds.Configuration#source(EmbedDataSource)]
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
                .map(source -> source.get(name, List.of(), Objects.requireNonNull(i18n)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny()
                .isPresent();
    }

    /// Builder for configuring the Embed API of JDA-Commands.
    ///
    /// # Embed Sources
    /// Use [#source(EmbedDataSource)] to add an [EmbedDataSource] that [Embed]s can be loaded from. You can have
    /// multiple [EmbedDataSource]s.
    ///
    /// Use [EmbedDataSource#file(Path)] to load embeds from a JSON file. The file must contain a single JSON object
    /// that contains all embeds that can be loaded as child objects. Every embed must have a unique name, the embed
    /// object must follow the Discord API format.
    /// ## Example
    /// ```json
    /// {
    ///   "example": {
    ///     "title": "Greetings",
    ///     "description": "Hello World!"
    ///   }
    /// }
    /// ```
    /// # Localization
    /// The Embed API supports localization via the [I18n] class. The embed fields can either contain a localization key
    /// a direct localization message in a format supported by the [Localizer] implementation.
    ///
    /// For the default [Localizer] implementation, which uses [Fluava], this could look like this:
    /// ```json
    /// {
    ///   "example": {
    ///     "title": "example-title", // localization key
    ///     "description": "Hello {$user}!" // localization message
    ///   }
    /// }
    /// ```
    ///
    /// # Global Placeholders
    /// Use [#placeholder(String, Object)] to define placeholders that will be globally available for any [Embed].
    public static class Configuration {

        private final List<EmbedDataSource> sources;
        private final List<Placeholder> placeholders;
        private final I18n i18n;

        /// Constructs a new embed configuration builder.
        public Configuration(I18n i18n) {
            this.i18n = i18n;
            sources = new ArrayList<>();
            placeholders = new ArrayList<>();
        }

        /// Adds a new global placeholder with the given key and value. Global placeholders will be available for any
        /// [Embed] loaded by this API.
        ///
        /// @param key the key of the placeholder
        /// @param value the value to replace the placeholder with
        /// @return this instance for fluent interface
        @NotNull
        public Embeds.Configuration placeholder(@NotNull String key, @NotNull Object value) {
            return placeholder(key, value::toString);
        }

        /// Adds a new global placeholder with the given key and value. Global placeholders will be available for any
        /// [Embed] loaded by this API.
        ///
        /// @param key the key of the placeholder
        /// @param supplier the [Supplier] to get the value from
        /// @return this instance for fluent interface
        @NotNull
        public Embeds.Configuration placeholder(@NotNull String key, @NotNull Supplier<Object> supplier) {
            placeholders.add(new Placeholder(key, supplier));
            return this;
        }

        /// Adds a new [EmbedDataSource] that [Embed]s can be loaded from.
        ///
        /// @param source the [EmbedDataSource] to add
        /// @return this instance for fluent interface
        @NotNull
        public Embeds.Configuration source(@NotNull EmbedDataSource source) {
            sources.add(source);
            return this;
        }

        /// Finalizes the configuration step.
        ///
        /// @implNote Turns this builder into an immutable [Embeds] instance.
        @NotNull
        public Embeds build() {
            return new Embeds(sources, placeholders, i18n);
        }
    }
}
