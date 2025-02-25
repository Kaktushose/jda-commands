package com.github.kaktushose.jda.commands.embeds;

import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

public record Embeds(@NotNull Collection<EmbedDataSource> sources,
                     @NotNull Collection<Embeds.Placeholder<?>> placeholders,
                     @NotNull Collection<Embeds.Localization> localizations)
        implements EmbedConfiguration {

    public Embeds() {
        this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public Embeds source(@NotNull EmbedDataSource source) {
        sources.add(source);
        return this;
    }

    public <T> Embeds placeholder(String key, Supplier<T> supplier) {
        placeholders.add(new Embeds.Placeholder<>(key, supplier));
        return this;
    }

    public Embeds localization(Locale locale, EmbedDataSource embedDataSource) {
        localizations.add(new Embeds.Localization(locale, embedDataSource));
        return this;
    }

    public boolean exists(String embed) {
        return sources.stream()
                .map(source -> source.get(embed))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny()
                .isPresent();
    }

    public Embed get(String embed) {
        return sources.stream()
                .map(source -> source.get(embed))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny()
                .map(Embed::new)
                .orElseThrow(() -> new IllegalStateException("Unknown embed " + embed));
    }

    private record Placeholder<T>(@NotNull String key, @NotNull Supplier<T> value) {}

    private record Localization(@NotNull Locale locale, @NotNull EmbedDataSource embedDataSource) {}

}
