package com.github.kaktushose.jda.commands.embeds;

import com.github.kaktushose.jda.commands.JDACBuilder.ConfigurationException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/// An [EmbedDataSource] is used to retrieve embeds based on a unique name from various sources.
@FunctionalInterface
public interface EmbedDataSource {

    /// Retrieves an [EmbedBuilder] based on the given name.
    ///
    /// @param embed the name of the embed to retrieve
    /// @return an [EmbedBuilder] constructed from the retrieved embed json
    /// @throws ParsingException If no value is present for the specified key
    @NotNull
    EmbedBuilder get(@NotNull String embed);

    /// Constructs a new [EmbedDataSource] using a JSON payload as its source.
    ///
    /// @param json the JSON payload to retrieve embeds from
    /// @return a new [EmbedDataSource]
    @NotNull
    static EmbedDataSource json(@NotNull String json) {
        return dataObject(DataObject.fromJson(json));
    }

    /// Constructs a new [EmbedDataSource] using a [Path] pointing to a JSON file as its source.
    ///
    /// @param path the [Path] pointing to a JSON file
    /// @return a new [EmbedDataSource]
    @NotNull
    static EmbedDataSource file(@NotNull Path path) {
        try {
            return inputStream(Files.newInputStream(path));
        } catch (IOException e) {
            throw new ConfigurationException("Failed to open file", e);
        }
    }

    /// Constructs a new [EmbedDataSource] using a [InputStream] as its source.
    ///
    /// @param inputStream the [InputStream] to retrieve embeds from
    /// @return a new [EmbedDataSource]
    @NotNull
    static EmbedDataSource inputStream(@NotNull InputStream inputStream) {
        return dataObject(DataObject.fromJson(inputStream));
    }

    /// Constructs a new [EmbedDataSource] using a [DataObject] as its source.
    ///
    /// @param dataObject the [DataObject] to retrieve embeds from
    /// @return a new [EmbedDataSource]
    @NotNull
    static EmbedDataSource dataObject(@NotNull DataObject dataObject) {
        return embed -> EmbedBuilder.fromData(dataObject.getObject(embed));
    }
}
