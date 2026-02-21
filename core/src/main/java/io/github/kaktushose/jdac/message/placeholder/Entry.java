package io.github.kaktushose.jdac.message.placeholder;

import io.github.kaktushose.jdac.message.i18n.I18n;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/// A placeholder identified by its name with the value to be substituted.
///
/// @param name  the placeholders name
/// @param value the value to be substituted
/// @see PlaceholderResolver
/// @see I18n
public record Entry(String name, @Nullable Object value) {

    /// Takes a varargs array of [Entries][Entry] and transforms it into a [Map], where [Entry#name()] is the key
    /// [Entry#value()] will be mapped to.
    ///
    /// @param placeholder the [Entries][Entry] to transform into a [Map]
    /// @return a [Map] representing the passed [Entries][Entry]
    public static Map<String, @Nullable Object> toMap(Entry... placeholder) {
        return Arrays.stream(placeholder)
                .collect(HashMap::new, (m, e) -> m.put(e.name(), e.value()), HashMap::putAll);
    }

    /// This method returns an [Entry] containing the name and value provided.
    /// It comes in handy when imported with a static import.
    ///
    /// @param name  the name of the placeholder
    /// @param value the value of the placeholder
    /// @return the [Entry] consisting of the name and value
    public static Entry entry(String name, @Nullable Object value) {
        return new Entry(name, value);
    }
}
