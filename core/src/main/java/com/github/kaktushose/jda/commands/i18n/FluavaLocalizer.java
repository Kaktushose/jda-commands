package com.github.kaktushose.jda.commands.i18n;

import dev.goldmensch.fluava.Bundle;
import dev.goldmensch.fluava.Fluava;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class FluavaLocalizer implements Localizer {

    private final ConcurrentHashMap<String, Bundle> cache = new ConcurrentHashMap<>();

    private final Fluava fluava;

    public FluavaLocalizer(Fluava fluava) {
        this.fluava = fluava;
    }

    @Override
    public String localize(Locale locale, String bundle, String key, Map<String, Object> arguments) {
        String formattedKey = key.replace('.', '-');
        String result = cache.computeIfAbsent(bundle, fluava::loadBundle).apply(locale, formattedKey, arguments);
        if (result.equals(formattedKey)) return key;
        return result;
    }
}
