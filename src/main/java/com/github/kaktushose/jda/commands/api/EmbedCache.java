package com.github.kaktushose.jda.commands.api;

import com.github.kaktushose.jda.commands.entities.EmbedDTO;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class loads and caches embeds from a json file.
 *
 * @author Kaktushose
 * @version 1.1.0
 * @since 1.1.0
 */
public class EmbedCache {

    private static final Logger log = LoggerFactory.getLogger(EmbedCache.class);
    private static final Gson gson = new Gson();
    private final File file;
    private Map<String, EmbedDTO> embedMap;

    /**
     * Constructs a new EmbedCache object.
     *
     * @param file the file to load the embeds from
     */
    public EmbedCache(File file) {
        embedMap = new ConcurrentHashMap<>();
        this.file = file;
    }

    /**
     * Loads all embeds from a file and caches them.
     */
    @SuppressWarnings("UnstableApiUsage")
    public void loadEmbedsToCache() {
        try {
            JsonReader jsonReader = new JsonReader(new FileReader(file));
            Type type = new TypeToken<Map<String, EmbedDTO>>() {
            }.getType();
            embedMap = gson.fromJson(jsonReader, type);
        } catch (FileNotFoundException | JsonIOException | JsonSyntaxException e) {
            log.error("An error has occurred while loading the file!", e);
        }
    }

    /**
     * Gets an embed from the cache.
     *
     * @param name the name the {@link EmbedDTO} is mapped to
     * @return the {@link EmbedDTO}
     */
    public EmbedDTO getEmbed(String name) {
        return embedMap.get(name);
    }

}
