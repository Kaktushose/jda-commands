package io.github.kaktushose.jdac.message.i18n.internal;

import io.github.kaktushose.jdac.definitions.interactions.command.CommandDefinition;
import io.github.kaktushose.jdac.introspection.Definitions;
import io.github.kaktushose.jdac.message.i18n.I18n;
import io.github.kaktushose.jdac.message.resolver.MessageResolver;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedCollection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ApiStatus.Internal
public final class JDACLocalizationFunction implements LocalizationFunction {

    public static final ScopedValue<Boolean> JDA_LOCALIZATION = ScopedValue.newInstance();
    private static final Logger log = LoggerFactory.getLogger(JDACLocalizationFunction.class);
    private static final Pattern OPTIONS_SEPARATOR = Pattern.compile("[.]options.*([.]name|[.]description)$");
    private final BundleFinder bundleFinder;
    private final Definitions definitions;
    private final MessageResolver resolver;

    public JDACLocalizationFunction(BundleFinder bundleFinder, Definitions definitions, MessageResolver resolver) {
        this.bundleFinder = bundleFinder;
        this.definitions = definitions;
        this.resolver = resolver;
    }

    @Override
    public Map<DiscordLocale, String> apply(String localizationKey) {
        String bundle = findBundle(localizationKey);

        HashMap<DiscordLocale, String> localizations = new HashMap<>();
        for (DiscordLocale locale : DiscordLocale.values()) {
            if (locale == DiscordLocale.UNKNOWN) continue;

            tryLocalize(bundle + "$" + localizationKey, locale) // with found bundle (or default)
                    .or(() -> tryLocalize(localizationKey, locale)) // fallback to default bundle if not found in special bundle
                    .or(() -> {
                        if (localizationKey.endsWith(".description")) {
                            String result = resolver.resolve("jdac$no-description", locale);
                            return Optional.of(result);
                        }
                        return Optional.empty();
                    })
                    .ifPresent(s -> localizations.put(locale, s));
        }

        return localizations;
    }

    private Optional<String> tryLocalize(String key, DiscordLocale locale) {
        String result = ScopedValue.where(JDA_LOCALIZATION, true).call(() -> resolver.resolve(key, locale));
        return result.equals(key)
                ? Optional.empty()
                : Optional.of(result);
    }

    private String findBundle(String key) {
        return extractCommand(key)
                .flatMap(this::findCommand)
                .map(CommandDefinition::classDescription)
                .map(bundleFinder::checkClass)
                .filter(bundle -> !bundle.isEmpty())
                .orElse(I18n.DEFAULT_BUNDLE);
    }

    private Optional<CommandDefinition> findCommand(String name) {
        SequencedCollection<CommandDefinition> found = definitions.find(CommandDefinition.class, def -> {
            String normalized = def.name().replace(" ", ".");
            return normalized.equals(name);
        });

        if (found.isEmpty()) {
            log.warn("Found no command for name {}, falling back to default bundle.", name);
            return Optional.empty();
        }

        if (found.size() > 1) {
            String foundNames = found.stream()
                    .map(CommandDefinition::name)
                    .collect(Collectors.joining(", "));

            log.warn("Found multiple commands for name {}, falling back to default bundle. Found commands: {}", name, foundNames);
            return Optional.empty();
        }

        return Optional.of(found.getFirst());

    }

    private Optional<String> extractCommand(String key) {
        Matcher matcher = OPTIONS_SEPARATOR.matcher(key);
        if (matcher.find()) {
            String stripped = matcher.replaceFirst("");
            return Optional.of(stripped);
        }

        if (key.endsWith(".name")) {
            return Optional.of(key.replaceFirst("[.]name$", ""));
        }

        if (key.endsWith(".description")) {
            return Optional.of(key.replaceFirst("[.]description$", ""));
        }

        log.warn("Couldn't extract command name out of jda localization key {}. Fallback to default bundle.", key);
        return Optional.empty();
    }
}
