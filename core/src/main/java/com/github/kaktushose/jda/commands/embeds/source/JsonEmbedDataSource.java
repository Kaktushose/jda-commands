package com.github.kaktushose.jda.commands.embeds.source;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class JsonEmbedDataSource implements EmbedDataSource {

    private static final Logger log = LoggerFactory.getLogger(JsonEmbedDataSource.class);
    private final Path path;

    public JsonEmbedDataSource(Path path) {
        this.path = path;
    }

    public Optional<EmbedBuilder> get(@NotNull String embed) {
        try (final var inputStream = Files.newInputStream(path)) {
            return Optional.of(EmbedBuilder.fromData(DataObject.fromJson(inputStream).getObject(embed)));
        } catch (IOException e) {
            log.error("Failed to open file!", e);
            return Optional.empty();
        }
    }

}
