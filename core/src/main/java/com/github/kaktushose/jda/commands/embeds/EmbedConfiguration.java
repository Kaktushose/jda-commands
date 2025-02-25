package com.github.kaktushose.jda.commands.embeds;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.Supplier;

public interface EmbedConfiguration {

    EmbedConfiguration source(@NotNull EmbedDataSource source);

    default EmbedConfiguration placeholder(String key, Object value) {
        return placeholder(key, () -> value);
    }

    <T> EmbedConfiguration placeholder(String key, Supplier<T> supplier);

    EmbedConfiguration localization(Locale locale, EmbedDataSource embedDataSource);

}
