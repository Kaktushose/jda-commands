package com.github.kaktushose.jda.commands.data;

import com.github.kaktushose.jda.commands.dispatching.GenericEvent;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple key value store for state management.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @since 2.3.0
 */
public class StateSection {

    private final Map<String, StateSection> sections;
    private final Map<String, Object> values;

    /**
     * Constructs a new StateSection.
     */
    public StateSection() {
        this.sections = new ConcurrentHashMap<>();
        this.values = new ConcurrentHashMap<>();
    }

    /**
     * Gets or creates a section from a {@link GenericEvent}. Creates a new StateSection if and only if no value is
     * present yet.
     *
     * @param event the {@link GenericEvent}
     * @return a StateSection
     */
    public StateSection section(GenericEvent event) {
        return section(event.getUser().getId());
    }

    /**
     * Gets or creates a section. Creates a new StateSection if and only if no value is present yet.
     *
     * @param key the key
     * @return a StateSection
     */
    public StateSection section(String key) {
        if (sections.containsKey(key)) {
            return sections.get(key);
        }
        StateSection section = new StateSection();
        sections.put(key, section);
        return section;
    }

    /**
     * Gets a value.
     *
     * @param key   the key
     * @param clazz the class of the value
     * @param <T>   the type of the value
     * @return an {@link Optional} holding the value
     */
    public <T> Optional<T> get(String key, Class<? extends T> clazz) {
        return Optional.ofNullable(values.get(key)).filter(it -> it.getClass().isAssignableFrom(clazz)).map(clazz::cast);
    }

    /**
     * Associates the specified value with the specified key.
     *
     * @param key   the key
     * @param value the value
     * @return this instance for fluent interface
     */
    public StateSection put(String key, Object value) {
        values.put(key, value);
        return this;
    }

    /**
     * Whether this StateSection has a value mapped to the key.
     *
     * @param key the key
     * @return {@code true} if this StateSection has a value mapped to the key
     */
    public boolean contains(String key) {
        return values.containsKey(key);
    }

    /**
     * Removes the mapping for a key from this {@link StateSection}. This can either be a value or another
     * {@link StateSection}.
     *
     * @param key key whose mapping is to be removed
     * @return this instance for fluent interface
     */
    public StateSection remove(String key) {
        values.remove(key);
        return this;
    }

    /**
     * Removes all of the mappings from this {@link StateSection} including values and
     * {@link StateSection StateSections}.
     *
     * @return this instance for fluent interface
     */
    public StateSection clear() {
        values.clear();
        sections.clear();
        return this;
    }

}
