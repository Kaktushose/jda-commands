package com.github.kaktushose.jda.commands.data;


import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface Repository<T> {

    long count();

    void delete(long id);

    void deleteAll(Collection<Long> ids);

    boolean existsById(long id);

    Collection<T> findAll();

    Optional<T> findById(long id);

    void save(long id, T entity);

    void saveAll(Map<Long, T> entities);

}
