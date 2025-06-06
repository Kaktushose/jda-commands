package com.github.kaktushose.jda.commands.i18n;

import com.github.kaktushose.jda.commands.annotations.i18n.Bundle;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.Description;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.i18n.internal.JDACLocalizationFunction;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.apache.commons.collections4.map.LRUMap;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class I18n {
    // TODO make this configurable
    private final LRUMap<Class<?>, String> cache = new LRUMap<>(64);

    private final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    public final String DEFAULT_BUNDLE = "default";

    private final Descriptor descriptor;
    private final Localizer localizer;
    private final LocalizationFunction localizationFunction = new JDACLocalizationFunction(this);

    public I18n(Descriptor descriptor, Localizer localizer) {
        this.descriptor = descriptor;
        this.localizer = localizer;
    }

    public String localize(Locale locale, String key, Map<String, Object> arguments) {
        String[] split = key.split("#", 1);
        String bundle = split.length == 2
                ? split[0].trim()
                : findBundle();

        return localizer.localize(locale, bundle, key, arguments);
    }

    private String findBundle() {
        return walker.walk(stream -> stream
                .map(this::checkFrame)
                .filter(b -> !b.isEmpty())
                .findAny()
        ).orElse(DEFAULT_BUNDLE);
    }

    private String checkFrame(StackWalker.StackFrame frame) {
        Class<?> klass = frame.getDeclaringClass();

        String name = klass.getName();
        if (name.startsWith("com.github.kaktushose.jda.commands") || name.startsWith("net.dv8tion.jda") || name.startsWith("java.")) {
            return "";
        }

        ClassDescription classDescription = descriptor.describe(klass);
        return classDescription.methods()
                .stream()
                .filter(method -> method.toMethodType().equals(frame.getMethodType()))
                .findFirst()
                .flatMap(this::readAnnotation)
                .orElseGet(() -> cache.computeIfAbsent(klass, _ -> readAnnotation(classDescription)
                                .orElseGet(() -> readAnnotation(classDescription.packageDescription()).orElse(""))
                        )
                );
    }

    private Optional<String> readAnnotation(Description description) {
        return description.annotation(Bundle.class)
                .map(Bundle::value);
    }

    public LocalizationFunction localizationFunction() {
        return localizationFunction;
    }
}
