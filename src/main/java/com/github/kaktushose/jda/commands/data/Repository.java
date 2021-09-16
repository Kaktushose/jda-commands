package com.github.kaktushose.jda.commands.data;


import java.util.Collection;
import java.util.Optional;

public interface Repository<T> {

    long count();

    void delete(T entity);

    void deleteAll(Collection<T> entities);

    boolean existsById(long id);

    Collection<T> findAll();

    Optional<T> findById(long id);

    void save(T entity);

    void saveAll(Collection<T> entities);

}
