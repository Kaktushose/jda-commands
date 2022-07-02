package com.github.kaktushose.jda.commands.data;

import com.github.kaktushose.jda.commands.dispatching.GenericEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private final long amount;
    private final TimeUnit unit;
    private final ScheduledExecutorService executorService;

    /**
     * Constructs a new StateSection.
     */
    public StateSection() {
        this.sections = new ConcurrentHashMap<>();
        this.values = new ConcurrentHashMap<>();
        amount = 0;
        unit = TimeUnit.MINUTES;
        executorService = Executors.newScheduledThreadPool(10);
    }

    /**
     * Constructs a new StateSection with a specified time to live for all values.
     *
     * @param amount the amount of time of the TTL
     * @param unit   the time unit of the delay parameter
     */
    public StateSection(long amount, @NotNull TimeUnit unit) {
        this.sections = new ConcurrentHashMap<>();
        this.values = new ConcurrentHashMap<>();
        this.amount = amount;
        this.unit = unit;
        executorService = Executors.newScheduledThreadPool(10);
    }

    /**
     * Gets or creates a section from a {@link GenericEvent}. Creates a new StateSection if and only if no value is
     * present yet.
     *
     * @param event the {@link GenericEvent}
     * @return a StateSection
     */
    public StateSection section(@NotNull GenericEvent event) {
        return section(event.getUser().getId());
    }

    /**
     * Gets or creates a section from a {@link GenericEvent} with a specified time to live for all values.
     * Creates a new StateSection if and only if no value is present yet.
     *
     * @param event  the {@link GenericEvent}
     * @param amount the amount of time of the TTL
     * @param unit   the time unit of the delay parameter
     * @return a StateSection
     */
    public StateSection section(@NotNull GenericEvent event, long amount, @NotNull TimeUnit unit) {
        return section(event.getUser().getId(), amount, unit);
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
        if (amount > 0) {
            executorService.schedule(() -> sections.remove(key), amount, unit);
        }
        return section;
    }

    /**
     * Gets or creates a section with a specified time to live for all values. Creates a new StateSection if and only
     * if no value is present yet.
     *
     * @param key    the key
     * @param amount the amount of the duration, measured in terms of the unit, positive or negative
     * @param unit   the unit that the duration is measured in, must have an exact duration, not null
     * @return a StateSection
     */
    public StateSection section(String key, long amount, @NotNull TimeUnit unit) {
        if (sections.containsKey(key)) {
            return sections.get(key);
        }
        StateSection section = new StateSection(amount, unit);
        sections.put(key, section);
        if (amount > 0) {
            executorService.schedule(() -> sections.remove(key), amount, unit);
        }
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
    public <T> Optional<T> get(String key, @NotNull Class<? extends T> clazz) {
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
        if (amount > 0) {
            executorService.schedule(() -> values.remove(key), amount, unit);
        }
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
     * Whether this StateSection has a {@link StateSection} mapped to the key.
     *
     * @param key the key
     * @return {@code true} if this StateSection has a value mapped to the key
     */
    public boolean containsSection(String key) {
        return sections.containsKey(key);
    }

    /**
     * Removes the value mapping for a key from this {@link StateSection}.
     *
     * @param key key whose mapping is to be removed
     * @return this instance for fluent interface
     */
    public StateSection remove(String key) {
        values.remove(key);
        return this;
    }

    /**
     * Removes the {@link StateSection} mapping for a key from this {@link StateSection}.
     *
     * @param key key whose mapping is to be removed
     * @return this instance for fluent interface
     */
    public StateSection removeSection(String key) {
        sections.remove(key);
        return this;
    }

    /**
     * Removes all the value mappings from this {@link StateSection}.
     *
     * @return this instance for fluent interface
     */
    public StateSection clear() {
        values.clear();
        return this;
    }

    /**
     * Removes all the {@link StateSection} mappings from this {@link StateSection}.
     *
     * @return this instance for fluent interface
     */
    public StateSection clearSections() {
        sections.clear();
        return this;
    }

}
