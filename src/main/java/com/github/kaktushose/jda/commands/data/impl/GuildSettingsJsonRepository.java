package com.github.kaktushose.jda.commands.data.impl;

import com.github.kaktushose.jda.commands.data.JsonRepository;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * An implementation of the {@link JsonRepository} interface to store {@link GuildSettings} in json format.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @since 2.0.0
 */
public class GuildSettingsJsonRepository extends JsonRepository<GuildSettings> {

    @SuppressWarnings("UnstableApiUsage")
    private static final Type mapType = new TypeToken<Map<Long, GuildSettings>>() {
    }.getType();

    /**
     * Constructs a new GuildJsonRepository.
     *
     * @param path the path of the file to save the json in
     */
    public GuildSettingsJsonRepository(@NotNull String path) {
        super(path, mapType);
    }

    /**
     * Constructs a new GuildJsonRepository.
     *
     * @param file the file to save the json in
     */
    public GuildSettingsJsonRepository(@NotNull File file) {
        super(file, mapType);
    }

    @Override
    public Collection<GuildSettings> findAll() {
        return map.values();
    }

    @Override
    public Optional<GuildSettings> findById(long id) {
        return Optional.ofNullable(map.get(id));
    }
}
