package io.github.kaktushose.jdac.message.i18n.internal;

import io.github.kaktushose.jdac.message.resolver.MessageResolver;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public final class JDACLocalizationFunction implements LocalizationFunction {

    public static final ScopedValue<Boolean> JDA_LOCALIZATION = ScopedValue.newInstance();

    private final MessageResolver resolver;

    public JDACLocalizationFunction(MessageResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public Map<DiscordLocale, String> apply(String localizationKey) {
        HashMap<DiscordLocale, String> localizations = new HashMap<>();
        for (DiscordLocale locale : DiscordLocale.values()) {
            if (locale == DiscordLocale.UNKNOWN) continue;


            String result = ScopedValue.where(JDA_LOCALIZATION, true).call(() -> resolver.resolve(localizationKey, locale.toLocale()));
            if (!result.equals(localizationKey)) {
                localizations.put(locale, result);
            } else if (localizationKey.contains("description")) {
                String noDescriptionLocalized = resolver.resolve("jdac$no-description", locale.toLocale());
                localizations.put(locale, noDescriptionLocalized);
            }
        }
        return localizations;
    }
}
