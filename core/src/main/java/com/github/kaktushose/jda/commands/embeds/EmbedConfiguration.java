package com.github.kaktushose.jda.commands.embeds;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.Supplier;

public interface EmbedConfiguration {

    @NotNull
    EmbedConfiguration source(@NotNull EmbedDataSource source);

    @NotNull
    default EmbedConfiguration placeholder(@NotNull String key, @NotNull Object value) {
        return placeholder(key, value::toString);
    }

    @NotNull
    EmbedConfiguration placeholder(@NotNull String key, @NotNull Supplier<String> supplier);

    @NotNull
    EmbedConfiguration localization(@NotNull Locale locale, @NotNull EmbedDataSource embedDataSource);

}
