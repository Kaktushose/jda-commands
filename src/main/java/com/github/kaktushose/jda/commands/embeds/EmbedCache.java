package com.github.kaktushose.jda.commands.embeds;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/// This class loads and caches embeds from a json file.
///
/// @see EmbedDTO
public class EmbedCache {

    private static final Logger log = LoggerFactory.getLogger(EmbedCache.class);
    private static final Gson gson = new Gson();
    private final File file;
    private final InputStream stream;
    private final Map<String, EmbedDTO> embedMap;

    /// Constructs a new EmbedCache object.
    ///
    /// @param file the file to load the embeds from
    public EmbedCache(File file) {
        embedMap = new ConcurrentHashMap<>();
        this.file = file;
        this.stream = null;
        loadEmbeds();
    }

    /// Constructs a new EmbedCache object.
    ///
    /// @param stream the stream to load the embeds from
    public EmbedCache(InputStream stream) {
        embedMap = new ConcurrentHashMap<>();
        this.stream = stream;
        this.file = null;
        loadEmbeds();
    }

    /// Constructs a new EmbedCache object.
    ///
    /// @param file the path to the file to load the embeds from
    public EmbedCache(String file) {
        embedMap = new ConcurrentHashMap<>();
        this.file = new File(file);
        this.stream = null;
        loadEmbeds();
    }

    /// Loads all embeds from a file and stores them. This happens automatically each time you construct a new
    /// EmbedCache. Thus, it's **not** needed to call this method manually, unless you want to reload the embeds.
    public void loadEmbeds() {
        try {
            Reader reader;
            if (file != null) {
                reader = new FileReader(file);
            } else if (stream != null) {
                reader = new InputStreamReader(stream);
            } else {
                throw new IllegalArgumentException("File and stream are null!");
            }

            JsonReader jsonReader = new JsonReader(reader);
            Type type = new TypeToken<Map<String, EmbedDTO>>() {
            }.getType();
            embedMap.clear();
            embedMap.putAll(gson.fromJson(jsonReader, type));
        } catch (FileNotFoundException | JsonIOException | JsonSyntaxException e) {
            log.error("An error has occurred while loading the file!", e);
        }
    }

    /// Gets an embed from the cache.
    ///
    /// @param name the name the [EmbedDTO] is mapped to
    /// @return the [EmbedDTO] or `null` if the cache contains no mapping for the key
    public EmbedDTO getEmbed(@Nonnull String name) {
        return new EmbedDTO(embedMap.get(name));
    }

    /// Returns `true` if this cache contains no [EmbedDTO]s.
    ///
    /// @return `true` if this cache contains no [EmbedDTO]s.
    public boolean isEmpty() {
        return embedMap.isEmpty();
    }

    /// Returns the number of [EmbedDTO]s in this cache.
    ///
    /// @return the number of [EmbedDTO]s in this cache.
    public int size() {
        return embedMap.size();
    }

    /// Returns `true` if this cache contains a mapping for the specified name.
    ///
    /// @param name the name the [EmbedDTO] is mapped to
    /// @return `true` if this cache contains a mapping for the specified name.
    public boolean containsEmbed(@Nonnull String name) {
        return embedMap.containsKey(name);
    }

    /// Returns an unmodifiable List containing all [EmbedDTO] of this cache.
    ///
    /// @return an unmodifiable List containing all [EmbedDTO] of this cache.
    public List<EmbedDTO> values() {
        return List.copyOf(embedMap.values());
    }

}
