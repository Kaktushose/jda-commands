package com.github.kaktushose.jda.commands.data;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public abstract class JsonRepository<T> implements Repository<T> {

    private static final Logger log = LoggerFactory.getLogger(JsonRepository.class);
    private final Gson gson;
    private final File file;
    protected Map<Long, T> map;

    public JsonRepository(String path) {
        this(new File(path));
    }

    public JsonRepository(File file) {
        this.file = file;
        gson = new Gson();
        map = new HashMap<>();
        if (!file.exists()) {
            try {
               file.createNewFile();
                log.debug("File didn't exist yet. Created a new one.");
            } catch (IOException e) {
                e.printStackTrace();
                log.error("Unable to create a new file!", e);
            }
            save();
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    protected void load() {
        Type type = new TypeToken<Map<Long, T>>() {}.getType();
        try (JsonReader reader = new JsonReader(new FileReader(file))) {
            map = gson.fromJson(reader, type);
            log.debug("Loaded values from file");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("An error has occurred while loading values!", e);
        }
    }

    protected void save() {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(map, writer);
            log.debug("Saved values to file");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("An error has occurred while saving values!", e);
        }
    }
}
