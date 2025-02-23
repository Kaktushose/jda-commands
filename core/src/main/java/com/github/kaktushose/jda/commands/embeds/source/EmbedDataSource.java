package com.github.kaktushose.jda.commands.embeds.source;

import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Optional;

public interface EmbedDataSource {

    Optional<EmbedBuilder> get(@NotNull String embed);

    static EmbedDataSource json(Path path) {
        return new JsonEmbedDataSource(path);
    }

}
