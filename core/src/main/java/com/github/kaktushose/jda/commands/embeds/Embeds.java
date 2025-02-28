package com.github.kaktushose.jda.commands.embeds;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

public record Embeds(@NotNull Collection<EmbedDataSource> sources,
                     @NotNull Collection<Embeds.Placeholder> placeholders,
                     @NotNull Collection<Embeds.Localization> localizations)
        implements EmbedConfiguration {

    public Embeds() {
        this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    @NotNull
    public Embeds source(@NotNull EmbedDataSource source) {
        sources.add(source);
        return this;
    }

    @NotNull
    public Embeds placeholder(@NotNull String key, @NotNull Supplier<String> supplier) {
        placeholders.add(new Embeds.Placeholder(key, supplier));
        return this;
    }

    @NotNull
    public Embeds localization(@NotNull Locale locale, @NotNull EmbedDataSource embedDataSource) {
        localizations.add(new Embeds.Localization(locale, embedDataSource));
        return this;
    }

    public boolean exists(String embed) {
        return sources.stream()
                .map(source -> source.get(embed, placeholders))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny()
                .isPresent();
    }

    @NotNull
    public Embed get(@NotNull String embed) {
        return sources.stream()
                .map(source -> source.get(embed, placeholders))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Unknown embed " + embed));
    }

    record Placeholder(@NotNull String key, @NotNull Supplier<String> value) {}

    record Localization(@NotNull Locale locale, @NotNull EmbedDataSource embedDataSource) {}

}
