package com.github.kaktushose.jda.commands.embeds;

import com.github.kaktushose.jda.commands.embeds.Embed.Placeholder;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

/// Container for immutably holding the embed configuration made by [Configuration].
///
/// @param sources the [EmbedDataSource]s [Embed]s can be loaded from
/// @param placeholders the global [Placeholder]s
public record Embeds(@NotNull Collection<EmbedDataSource> sources, @NotNull Collection<Placeholder> placeholders) {

    public Embeds {
        sources = Collections.unmodifiableCollection(sources);
        placeholders = Collections.unmodifiableCollection(placeholders);
    }

    /// Constructs an empty [Embeds] container with no [EmbedDataSource]s or [Placeholder]s registered.
    @NotNull
    public static Embeds empty() {
        return new Embeds(Collections.emptyList(), Collections.emptyList());
    }

    /// Gets an [Embed] based on the given name. Will apply all [#placeholders()].
    ///
    /// @param name the name of the [Embed]
    /// @return the [Embed]
    /// @throws IllegalArgumentException if no [Embed] with the given name exists in the configured
    /// [data sources][com.github.kaktushose.jda.commands.embeds.Embeds.Configuration#source(EmbedDataSource)]
    @NotNull
    public Embed get(@NotNull String name) {
        return sources.stream()
                .map(source -> source.get(name, placeholders))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unknown embed " + name));
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
    ///
    /// Global Placeholders
    /// You can define placeholders inside the JSON object. They must follow the format: `{key}`. Placeholders that don't
    /// get replaced will log an error, unless they are optional, denoted by an `:o` prefix: `{o:key}`. Use
    /// [#placeholder(String, Object)] to define placeholders that will be globally available for any [Embed].
    public static class Configuration {

        private final List<EmbedDataSource> sources;
        private final List<Placeholder> placeholders;

        /// Constructs a new embed configuration builder.
        public Configuration() {
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
        public Embeds.Configuration placeholder(@NotNull String key, @NotNull Supplier<String> supplier) {
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
            return new Embeds(sources, placeholders);
        }
    }
}
