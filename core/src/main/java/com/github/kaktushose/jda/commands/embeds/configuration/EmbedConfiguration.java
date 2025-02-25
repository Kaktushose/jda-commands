package com.github.kaktushose.jda.commands.embeds.configuration;

import com.github.kaktushose.jda.commands.embeds.EmbedDataSource;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

public record EmbedConfiguration(@NotNull Collection<EmbedDataSource> sources,
                                 @NotNull Collection<EmbedConfiguration.Placeholder<?>> placeholders,
                                 @NotNull Collection<EmbedConfiguration.Localization> localizations)
        implements EmbedConfigurationStage {

    public EmbedConfiguration() {
        this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public EmbedConfiguration source(@NotNull EmbedDataSource source) {
        sources.add(source);
        return this;
    }

    public <T> EmbedConfiguration placeholder(String key, Supplier<T> supplier) {
        placeholders.add(new EmbedConfiguration.Placeholder<>(key, supplier));
        return this;
    }

    public EmbedConfiguration localization(Locale locale, EmbedDataSource embedDataSource) {
        localizations.add(new EmbedConfiguration.Localization(locale, embedDataSource));
        return this;
    }

    public EmbedBuilder get(String embed) {
        return sources.stream()
                .map(source -> source.get(embed))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Unknown embed " + embed));
    }

    private record Placeholder<T>(@NotNull String key, @NotNull Supplier<T> value) {}

    private record Localization(@NotNull Locale locale, @NotNull EmbedDataSource embedDataSource) {}

}
