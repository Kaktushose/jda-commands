package com.github.kaktushose.jda.commands.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Map;

public class DefaultLocalizer implements Localizer{
    private static final Logger log = LoggerFactory.getLogger(DefaultLocalizer.class);

    private final Map<String, String> germanLocales = Map.of(
            "press me", "Drück mich ;)"
    );

    @Override
    public String localize(Locale locale, String key, Map<String, Object> arguments) {
        String found = germanLocales.getOrDefault(key.toLowerCase(), key);
        log.debug("Tried to localize {} ({}) with arguments {} for locale {}. Found {}", key, key.toLowerCase(), arguments, locale, found);
        return found;
    }
}
