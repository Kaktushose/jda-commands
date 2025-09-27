package com.github.kaktushose.jda.commands.message.placeholder;

import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/// A placeholder identified by its name with the value to be substituted.
///
/// Placeholders of message with the given name are replaced by the given value during localization.
///
/// @param name the placeholders name
/// @param value the value to be substituted
public record Entry(String name, @Nullable Object value) {
    public static Map<String, @Nullable Object> toMap(Entry... placeholder) {
        return Arrays.stream(placeholder)
                .collect(HashMap::new, (m, e) -> m.put(e.name(), e.value()), HashMap::putAll);
    }

    /// This method returns an [Entry] containing the name and value provided.
    /// It comes in handy when imported with a static import.
    ///
    /// @param name the name of the placeholder
    /// @param value the value of the placeholder
    ///
    /// @return the [Entry] consisting of the name and value
    public static Entry entry(String name, @Nullable Object value) {
        return new Entry(name, value);
    }
}
