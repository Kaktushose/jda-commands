package com.github.kaktushose.jda.commands.embeds;


import com.github.kaktushose.jda.commands.exceptions.ConfigurationException;
import com.github.kaktushose.jda.commands.i18n.I18n;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import static com.github.kaktushose.jda.commands.i18n.I18n.entry;

/// An [EmbedDataSource] is used to retrieve [Embed]s based on a unique name from various sources.
@FunctionalInterface
public interface EmbedDataSource {

    /// Constructs a new [EmbedDataSource] using a JSON payload as its source.
    ///
    /// @param json the JSON payload to retrieve embeds from
    /// @return a new [EmbedDataSource]
    static EmbedDataSource json(String json) {
        return dataObject(DataObject.fromJson(json));
    }

    /// Constructs a new [EmbedDataSource] using a JSON file that is located inside the resources folder.
    ///
    /// @param resource the [Path] pointing to a JSON file
    /// @return a new [EmbedDataSource]
    static EmbedDataSource resource(String resource) {
        try (InputStream inputStream = EmbedDataSource.class.getClassLoader().getResourceAsStream(resource)) {
            if (inputStream == null) {
                throw new ConfigurationException("resource-not-found", entry("resource", resource));
            }
            return inputStream(inputStream);
        } catch (IOException e) {
            throw new ConfigurationException("io-exception", e);
        }
    }

    /// Constructs a new [EmbedDataSource] using a [Path] pointing to a JSON file as its source.
    ///
    /// @param path the [Path] pointing to a JSON file
    /// @return a new [EmbedDataSource]
    static EmbedDataSource file(Path path) {
        try {
            return json(Files.readString(path));
        } catch (IOException e) {
            throw new ConfigurationException("io-exception", e);
        }
    }

    /// Constructs a new [EmbedDataSource] using an JSON [InputStream] as its source.
    ///
    /// @param inputStream the [InputStream] to retrieve embeds from
    /// @return a new [EmbedDataSource]
    static EmbedDataSource inputStream(InputStream inputStream) {
        return dataObject(DataObject.fromJson(inputStream));
    }

    /// Constructs a new [EmbedDataSource] using a [DataObject] as its source.
    ///
    /// @param dataObject the [DataObject] to retrieve embeds from
    /// @return a new [EmbedDataSource]
    static EmbedDataSource dataObject(DataObject dataObject) {
        return (embed, placeholders, i18n) -> {
            if (!dataObject.hasKey(embed)) {
                return Optional.empty();
            }
            return Optional.of(new Embed(dataObject.getObject(embed), embed, placeholders, i18n));
        };
    }

    /// Retrieves an [Embed] based on the given name.
    ///
    /// @param embed        the name of the embed to retrieve
    /// @param placeholders a [Map] of placeholders to use
    /// @param i18n         the [I18n] instance to use
    /// @return an [Optional] holding the [Embed] constructed from the retrieved embed json or an empty [Optional]
    /// if no embed was found for the given name
    /// @throws ParsingException If the embed json is incorrect
    Optional<Embed> get(String embed, Map<String, Object> placeholders, I18n i18n);
}
