package com.github.kaktushose.jda.commands.embeds;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

public record EmbedConfiguration(@NotNull Collection<EmbedDataSource> sources,
                                 @NotNull Collection<Embed.Placeholder> placeholders) {

    public EmbedConfiguration {
        sources = Collections.unmodifiableCollection(sources);
        placeholders = Collections.unmodifiableCollection(placeholders);
    }

    @NotNull
    public Embed get(@NotNull String name) {
        return sources.stream()
                .map(source -> source.get(name, placeholders))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Unknown embed " + name));
    }

    public static class Builder {

        private final List<EmbedDataSource> sources;
        private final List<Embed.Placeholder> placeholders;

        public Builder() {
            sources = new ArrayList<>();
            placeholders = new ArrayList<>();
        }

        @NotNull
        public Builder placeholder(@NotNull String key, @NotNull Object value) {
            return placeholder(key, value::toString);
        }

        @NotNull
        public Builder source(@NotNull EmbedDataSource source) {
            sources.add(source);
            return this;
        }

        @NotNull
        public Builder placeholder(@NotNull String key, @NotNull Supplier<String> supplier) {
            placeholders.add(new Embed.Placeholder(key, supplier));
            return this;
        }

        @NotNull
        public EmbedConfiguration build() {
            return new EmbedConfiguration(sources, placeholders);
        }
    }
}
