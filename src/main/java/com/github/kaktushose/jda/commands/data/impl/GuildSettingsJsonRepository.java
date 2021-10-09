package com.github.kaktushose.jda.commands.data.impl;

import com.github.kaktushose.jda.commands.data.JsonRepository;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import com.google.common.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class GuildSettingsJsonRepository extends JsonRepository<GuildSettings> {

    @SuppressWarnings("UnstableApiUsage")
    private static final Type mapType = new TypeToken<Map<Long, GuildSettings>>() {
    }.getType();

    public GuildSettingsJsonRepository(String path) {
        super(path, mapType);
    }

    public GuildSettingsJsonRepository(File file) {
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
