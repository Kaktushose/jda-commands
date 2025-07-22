package com.github.kaktushose.jda.commands.i18n;

import com.github.kaktushose.jda.commands.annotations.i18n.Bundle;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.Description;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.i18n.internal.JDACLocalizationFunction;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.apache.commons.collections4.map.LRUMap;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;


/// This class serves as an interface for application localization.
///
/// It is mostly a wrapper around [Localizer] but supports flexible specification of the bundle to be used.
///
/// To state which bundle to use the direct way is to include it in the key following the format `bundle#key`.
/// For example a message with key `user#not-found` will be searched for in the bundle `user` and the key `not-found`.
///
/// If no bundle is specified, it will traverse the stack (the called methods) and search for the nearest
/// [`@Bundle("mybundle")`](Bundle) annotation with following order:
///
/// 1. method that called [I18n#localize(Locale, String, Entry...)]
/// 2. other called methods in the same class
/// 3. this method's class
/// 4. the class' package's `package-info.java` file
///
/// If no annotation is found, the previous method (in another class) is searched with the same pattern up to the
/// class at the very beginning.
///
/// If even after this no bundle name could be found, the bundle `default` will be used.
///
/// ### Example
/// `A.java`:
/// ```java
/// package my.app;
/// class A {
///     void aOne() {
///         i18n.localize(Locale.GERMAN, "fail", Map.of())
///     }
///
///     void aTwo() {
///         aOne();
///     }
/// }
/// ```
///
/// `B.java`:
/// ```java
/// package my.app.other;
///
/// @Bundle("b_bundle")
/// class B {
///     A another = new A();
///
///     void bOne() {
///         a.aOne();
///     }
///
///     @Bundle("mB_bundle")
///     void bTwo() {
///         bOne();
///     }
/// }
///
/// ```
///
/// `package-info.java`:
/// ```java
/// @Bundle("pack_bundle")
/// package my.app;
///
/// ```
///
/// The order in which the bundle name is searched for is following:
/// 1. method `A#aOne()`
/// 2. method `A#aTwo()`
/// 3. class `A`
/// 4. `package-info.java` of package `my.app`
/// 5. method `B#bOne()`
/// 6. method `B#two()`
///
/// The found bundle would be `pack_bundle`.
///
/// If [I18n#localize(java.util.Locale, java.lang.String, com.github.kaktushose.jda.commands.i18n.I18n.Entry...)]
/// would be called in, for example, `B#bTwo` the bundle would be `mB_bundle`.
public class I18n {


    // skipped classes during stack scanning (Class.getName().startWith(X))
    private static List<String> SKIPPED = List.of(
            "com.github.kaktushose.jda.commands",
            "net.dv8tion.jda",
            "java."
    );


    // TODO make this configurable
    private final LRUMap<Class<?>, String> cache = new LRUMap<>(64);

    private final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    public final String DEFAULT_BUNDLE = "default";

    private final Descriptor descriptor;
    private final Localizer localizer;
    private final LocalizationFunction localizationFunction = new JDACLocalizationFunction(this);
    private final ThreadLocal<ClassDescription> last = new ThreadLocal<>();


    public I18n(Descriptor descriptor, Localizer localizer) {
        this.descriptor = descriptor;
        this.localizer = localizer;
    }

    // default split (@) is undocumented and will be replaced in the future by solution with ScopedValues
    public String localize(Locale locale, String key, Map<String, Object> arguments) {
        String[] split = key.split("#", 2);
        String bundle = split.length == 2
                ? split[0].trim()
                : findBundle();

        String[] defaultSplit = key.split("@", 2);

        String localized = localizer.localize(locale, bundle, split[0], arguments);
        if (defaultSplit.length == 2 && localized.equals(split[0])) return defaultSplit[1];
        return localized;
    }

    public String localize(Locale locale, String key, Entry... placeholder) {
        Map<String, Object> map = Arrays.stream(placeholder)
                .collect(Collectors.toUnmodifiableMap(Entry::name, Entry::value));
        return localize(locale, key, map);
    }

    public static Entry entry(String name, Object value) {
        return new Entry(name, value);
    }

    public record Entry(String name, Object value) {}

    private String findBundle() {
        return walker.walk(stream -> stream
                .map(this::checkFrame)
                .filter(b -> !b.isEmpty())
                .findAny()
        ).orElseGet(() -> {
            String found = checkClass(last.get());
            return found.isEmpty()
                    ? DEFAULT_BUNDLE
                    : found;
        });
    }

    private String checkFrame(StackWalker.StackFrame frame) {
        Class<?> klass = frame.getDeclaringClass();

        String name = klass.getName();

        // just some optimization
        if (SKIPPED.stream().anyMatch(name::startsWith)) {
            return "";
        }

        ClassDescription classDescription = descriptor.describe(klass);

        ClassDescription lastDes = last.get();
        if (lastDes != null && !lastDes.clazz().equals(classDescription.clazz())) {
            String found = checkClass(lastDes);
            if (!found.isEmpty()) return found;
        }

        last.set(classDescription);

        return classDescription.methods()
                .stream()
                .filter(method -> method.toMethodType().equals(frame.getMethodType()))
                .findFirst()
                .flatMap(this::readAnnotation)
                .orElse("");
    }

    private String checkClass(@Nullable ClassDescription classDescription) {
        if (classDescription == null) return "";
        return cache.computeIfAbsent(classDescription.clazz(), _ -> readAnnotation(classDescription)
                                .orElseGet(() -> readAnnotation(classDescription.packageDescription()).orElse("")));
    }

    private Optional<String> readAnnotation(Description description) {
        return description.annotation(Bundle.class)
                .map(Bundle::value);
    }

    /// @return the [LocalizationFunction] bases on this class, for use with JDA
    public LocalizationFunction localizationFunction() {
        return localizationFunction;
    }
}
