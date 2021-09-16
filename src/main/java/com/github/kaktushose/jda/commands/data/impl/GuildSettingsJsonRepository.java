package com.github.kaktushose.jda.commands.data.impl;

import com.github.kaktushose.jda.commands.data.JsonRepository;
import com.github.kaktushose.jda.commands.settings.GuildSettings;

import java.util.Collection;
import java.util.Optional;

public class GuildSettingsJsonRepository extends JsonRepository<GuildSettings> {

    public GuildSettingsJsonRepository(String path) {
        super(path);
        load();
    }

    @Override
    public long count() {
        return map.size();
    }

    @Override
    public void delete(GuildSettings entity) {
        map.remove(entity.getGuildId());
        save();
    }

    @Override
    public void deleteAll(Collection<GuildSettings> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public boolean existsById(long id) {
        return map.containsKey(id);
    }

    @Override
    public Collection<GuildSettings> findAll() {
        return map.values();
    }

    @Override
    public Optional<GuildSettings> findById(long id) {
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public void save(GuildSettings entity) {
        map.put(entity.getGuildId(), entity);
        save();
    }

    @Override
    public void saveAll(Collection<GuildSettings> entities) {
        entities.forEach(this::save);
    }
}
