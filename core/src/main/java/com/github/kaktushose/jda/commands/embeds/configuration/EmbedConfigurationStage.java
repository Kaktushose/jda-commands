package com.github.kaktushose.jda.commands.embeds.configuration;

import com.github.kaktushose.jda.commands.embeds.EmbedDataSource;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.Supplier;

public interface EmbedConfigurationStage {

    EmbedConfigurationStage source(@NotNull EmbedDataSource source);

    default EmbedConfigurationStage placeholder(String key, Object value) {
        return placeholder(key, () -> value);
    }

    <T> EmbedConfigurationStage placeholder(String key, Supplier<T> supplier);

    EmbedConfigurationStage localization(Locale locale, EmbedDataSource embedDataSource);

}
