package com.github.kaktushose.jda.commands.data;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class JsonRepository<T> implements Repository<T> {

    private static final Logger log = LoggerFactory.getLogger(JsonRepository.class);
    private final Gson gson;
    private final File file;
    private final Type mapType;
    protected Map<Long, T> map;

    public JsonRepository(String path, Type mapType) {
        this(new File(path), mapType);
    }

    public JsonRepository(File file, Type mapType) {
        this.file = file;
        this.mapType = mapType;
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
        load();
    }

    protected void load() {
        try (JsonReader reader = new JsonReader(new FileReader(file))) {
            map = gson.fromJson(reader, mapType);
            System.out.println(mapType);
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

    @Override
    public long count() {
        return map.size();
    }

    @Override
    public void delete(long id) {
        map.remove(id);
        save();
    }

    @Override
    public void deleteAll(Collection<Long> ids) {
        ids.forEach(this::delete);
    }

    @Override
    public boolean existsById(long id) {
        return map.containsKey(id);
    }

    @Override
    public void save(long id, T entity) {
        map.put(id, entity);
        save();
    }

    @Override
    public void saveAll(Map<Long, T> entities) {
        entities.forEach(this::save);
    }
}
