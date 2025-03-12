package com.github.kaktushose.jda.commands.i18n;

import java.util.Locale;

public record I18nData(
        Localizer localizer,
        Locale locale,
        Localizer.Entry[] arguments
) {

    public String translate(String key) {
        return localizer.localize(locale, key, arguments);
    }
}
