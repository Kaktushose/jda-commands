package com.github.kaktushose.jda.commands.i18n;

import com.github.kaktushose.jda.commands.extension.Implementation;

import java.util.Locale;
import java.util.Map;

public non-sealed interface Localizer extends Implementation.ExtensionProvidable {
    String localize(Locale locale, String base, String key, Map<String, Object> arguments);

    static Entry entry(String name, Object value) {
        return new Entry(name, value);
    }

    record Entry(String name, Object value) {}

//    default String localize(Locale locale, String bundle, String key, Localizer.Entry... entries) {
//        Map<String, Object> arguments = entries != null
//                ? Arrays.stream(entries).collect(Collectors.toMap(Localizer.Entry::name, Localizer.Entry::value))
//                : Map.of();
//        return localize(locale, bundle, key, arguments);
//    }
}
