package com.github.kaktushose.jda.commands.i18n;

import com.github.kaktushose.jda.commands.extension.Implementation;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public non-sealed interface Localizer extends Implementation.ExtensionProvidable {
    String DEFAULT_BUNDLE = "default";

    String localize(Locale locale, String base, String key, Map<String, Object> arguments);

    default String localize(Locale locale, String key, Map<String, Object> arguments) {
        String[] split = key.split("#", 1);
        String bundle = split.length == 2
                ? split[0].trim()
                : DEFAULT_BUNDLE;

        return localize(locale, bundle, key, arguments);
    }

    default String localize(Locale locale, String key, Entry... entries) {
        Map<String, Object> arguments = entries != null
                ? Arrays.stream(entries).collect(Collectors.toMap(Entry::name, Entry::value))
                : Map.of();
        return localize(locale, key, arguments);
    }

    static Entry entry(String name, Object value) {
        return new Entry(name, value);
    }

    record Entry(String name, Object value) {}
}
