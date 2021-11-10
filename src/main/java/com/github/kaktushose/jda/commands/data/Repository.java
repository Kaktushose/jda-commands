package com.github.kaktushose.jda.commands.data;


import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Generic top level interface for performing CRUD operations on different persistence structures. Always uses
 * {@code long} for the id.
 *
 * @param <T> the type the repository manages
 * @author Kaktushose
 * @version 2.0.0
 * @since 2.0.0
 */
public interface Repository<T> {

    /**
     * Returns the entity count.
     *
     * @return the entity count
     */
    long count();

    /**
     * Delete an entity by id.
     *
     * @param id the id of the entity
     */
    void delete(long id);

    /**
     * Delete all given entities by id
     *
     * @param ids collection of the ids
     */
    void deleteAll(Collection<Long> ids);

    /**
     * Returns whether an entity with the given id exists.
     *
     * @param id the id to check
     * @return {@code} true if the entity exists
     */
    boolean existsById(long id);

    /**
     * Returns all entities inside the repository.
     *
     * @return all entities inside the repository
     */
    Collection<T> findAll();

    /**
     * Retrieves an entity by id.
     *
     * @param id the id of the entity
     * @return the entity with the given id or an empty Optional if none found.
     */
    Optional<T> findById(long id);

    /**
     * Saves a given entity.
     *
     * @param id     the id of the entity to save
     * @param entity the entity to save
     */
    void save(long id, T entity);

    /**
     * Saves all given entities.
     *
     * @param entities a Map containing all entities to save
     */
    void saveAll(Map<Long, T> entities);
}
