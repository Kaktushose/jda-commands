package com.github.kaktushose.jda.commands.i18n.internal;

import com.github.kaktushose.jda.commands.i18n.Localizer;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class JDACLocalizationFunction implements LocalizationFunction {

    private final Localizer localizer;

    public JDACLocalizationFunction(Localizer localizer) {
        this.localizer = localizer;
    }

    @Override
    public @NotNull Map<DiscordLocale, String> apply(@NotNull String localizationKey) {
        HashMap<DiscordLocale, String> localizations = new HashMap<>();
        for (DiscordLocale locale : DiscordLocale.values()) {
            if (locale == DiscordLocale.UNKNOWN) continue;

            String result = localizer.localize(locale.toLocale(), localizationKey, Map.of());
            if (!result.equals(localizationKey)) localizations.put(locale, result);
        }
        return localizations;
    }
}
