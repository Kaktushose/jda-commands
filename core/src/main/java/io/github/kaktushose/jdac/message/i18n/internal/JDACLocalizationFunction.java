package io.github.kaktushose.jdac.message.i18n.internal;

import io.github.kaktushose.jdac.message.i18n.I18n;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public final class JDACLocalizationFunction implements LocalizationFunction {

    public static final ScopedValue<Boolean> JDA_LOCALIZATION = ScopedValue.newInstance();

    private final I18n localizer;

    public JDACLocalizationFunction(I18n localizer) {
        this.localizer = localizer;
    }

    @Override
    public Map<DiscordLocale, String> apply(String localizationKey) {
        HashMap<DiscordLocale, String> localizations = new HashMap<>();
        for (DiscordLocale locale : DiscordLocale.values()) {
            if (locale == DiscordLocale.UNKNOWN) continue;


            String result = ScopedValue.where(JDA_LOCALIZATION, true).call(() -> localizer.resolve(localizationKey, locale.toLocale(), Map.of()));
            if (!result.equals(localizationKey)) {
                localizations.put(locale, result);
            } else if (localizationKey.contains("description")) {
                String noDescriptionLocalized = localizer.resolve("jdac$no-description", locale.toLocale(), Map.of());
                localizations.put(locale, noDescriptionLocalized);
            }
        }
        return localizations;
    }
}
