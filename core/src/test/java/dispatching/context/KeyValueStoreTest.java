package dispatching.context;

import io.github.kaktushose.jdac.dispatching.context.KeyValueStore;
import io.github.kaktushose.jdac.dispatching.context.KeyValueStore.Key;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class KeyValueStoreTest {

    private KeyValueStore store;

    @BeforeEach
    void setUp() {
        store = new KeyValueStore();
    }

    @Test
    void putAndGetWithKeyString() {
        store.put("key", "value");
        Optional<String> result = store.get("key", String.class);
        assertTrue(result.isPresent());
        assertEquals("value", result.get());
    }

    @Test
    void putAndGetWithKeyStringWithSuperclass() {
        store.put("key", 1);
        Optional<Number> result = store.get("key", Number.class);
        assertTrue(result.isPresent());
        assertEquals(1, result.get());
    }

    @Test
    void getWithWrongType_ShouldReturnEmpty() {
        store.put("key", "value");
        Optional<Integer> result = store.get("key", Integer.class);
        assertFalse(result.isPresent());
    }

    @Test
    void getWithGenericCast() {
        store.put("key", "value");
        Optional<String> result = store.get("key");
        assertTrue(result.isPresent());
        assertEquals("value", result.get());
    }

    @Test
    void putAndGetWithKey() {
        Key<String> key = new Key<>("key", String.class);
        store.put(key, "value");

        Optional<String> result = store.get(key);
        assertTrue(result.isPresent());
        assertEquals("value", result.get());
    }

    @Test
    void containsWithKeyString() {
        store.put("key", "value");
        assertTrue(store.contains("key"));
        assertFalse(store.contains("other"));
    }

    @Test
    void containsWithKey() {
        Key<String> key = new Key<>("key", String.class);
        Key<Integer> wrongTypeKey = new Key<>("key", Integer.class);

        store.put(key, "value");

        assertTrue(store.contains(key));
        assertFalse(store.contains(wrongTypeKey));
    }

    @Test
    void removeWithKeyString() {
        store.put("key", "value");
        store.remove("key");
        assertFalse(store.contains("key"));
    }

    @Test
    void removeWithKey() {
        Key<String> key = new Key<>("key", String.class);
        store.put(key, "value");
        store.remove(key);
        assertFalse(store.contains("key"));
    }

    @Test
    void clear_ShouldRemoveAllValues() {
        store.put("key1", "value1");
        store.put("key2", 2);

        store.clear();

        assertFalse(store.contains("key1"));
        assertFalse(store.contains("key2"));
    }

    @Test
    void Key_isAssignable() {
        Key<Number> key = new Key<>("key", Number.class);
        assertTrue(key.isAssignable(10)); // Integer is Number
        assertTrue(key.isAssignable(10.5)); // Double is Number
        assertFalse(key.isAssignable("string"));
    }

    @Test
    void getWithKey_Inheritance() {
        Key<Number> key = new Key<>("key", Number.class);
        store.put(key, 10); // Stores Integer

        Optional<Number> result = store.get(key);
        assertTrue(result.isPresent());
        assertEquals(10, result.get());
    }
}
