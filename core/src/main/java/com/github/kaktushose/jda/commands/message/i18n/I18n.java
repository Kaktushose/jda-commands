package com.github.kaktushose.jda.commands.message.i18n;

import com.github.kaktushose.jda.commands.annotations.i18n.Bundle;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.Description;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.message.i18n.internal.JDACLocalizationFunction;
import com.github.kaktushose.jda.commands.message.placeholder.Entry;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.apache.commons.collections4.map.LRUMap;
import org.jspecify.annotations.Nullable;

import java.util.*;


/// This class serves as an interface for application localization.
///
/// It is mostly a wrapper around [Localizer] but supports flexible specification of the bundle to be used.
///
/// To state which bundle to use the direct way is to include it in the key following the format `bundle$key`.
/// For example a message with key `user$not-found` will be searched for in the bundle `user` and the key `not-found`.
///
/// ### dollar sign
/// The dollar (`$`) is a reserved character for [bundle name separation](#bundles).
///
/// Practically, in all cases this doesn't really bother, there are only 2 niche situations where the dollar has to be escaped:
///   - your message key contains `$` and no bundle is explicitly stated, e.g. `key.with$.in.it`
///   - the string is a directly inserted localization messages containing `$`, that happens to have it's prior `$` part to match a bundle name and its after `$` part to match a message key, e.g.
///     - you have a bundle called `my_bundle`
///     - you have a message key called `my-key` in that bundle
///     - and you want to print the message `my_bundle$my-key` to the user (not the message stored under "my-key" in the bundle "my_bundle")
///
/// In these cases just prefix your whole message with a `$`, e.g. `$my_bundle$my-key` or `$key.with$.in.it`.
/// Now the bundle will be treated as not stated explicitly and the dollar sign will be preserved.
///
/// ## bundle name traversal
/// If no bundle is specified, it will traverse the stack (the called methods) and search for the nearest
/// [`@Bundle("mybundle")`](Bundle) annotation with following order:
///
///
/// 1. method that called [I18n#localize(Locale, String, Entry...)]
/// 2. other called methods in the same class
/// 3. this methods class
/// 4. the class' packages `package-info.java` file
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
/// 1. method `A$aOne()`
/// 2. method `A$aTwo()`
/// 3. class `A`
/// 4. `package-info.java` of package `my.app`
/// 5. method `B$bOne()`
/// 6. method `B$two()`
///
/// The found bundle would be `pack_bundle`.
///
/// If [I18n#localize(java.util.Locale, java.lang.String, Entry...)]
/// would be called in, for example, `B$bTwo` the bundle would be `mB_bundle`.
public class I18n {

    // skipped classes during stack scanning (Class.getName().startWith(X))
    private static final List<String> SKIPPED = List.of(
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

    /// @param descriptor the [Description] to be used to get the [Bundle] annotation
    /// @param localizer the used [Localizer] to retrieve the messages
    public I18n(Descriptor descriptor, Localizer localizer) {
        this.descriptor = descriptor;
        this.localizer = localizer;
    }

    /// This method returns the localized method found by the provided [Locale] and key
    /// in the given bundle.
    ///
    /// The bundle can be either explicitly stated by adding it to the
    /// key in the following format: `bundle$key`. Alternatively, the bundle name can also be
    /// contextual retrieved by a search for the [Bundle] annotation, see class docs.
    ///
    /// Please note that the character `$` is forbidden in bundle names.
    ///
    /// @param locale the [Locale] to be used to localize the key
    /// @param combinedKey the messages key
    /// @param placeholder the placeholder to be used
    ///
    /// @return the localized message or the key if not found
    public String localize(Locale locale, String combinedKey, Map<String, @Nullable Object> placeholder) {
        String[] bundleSplit = combinedKey.split("\\$", 2);
        String bundle = bundleSplit.length == 2 && !bundleSplit[0].isEmpty()
                ? bundleSplit[0].trim()
                : findBundle();

        String key = bundleSplit.length == 2
                ? bundleSplit[1]
                : bundleSplit[0];

        return localizer.localize(locale, bundle, key, placeholder)
                .orElse(combinedKey);
    }

    /// This method returns the localized message found by the provided [Locale] and key
    /// in the given bundle.
    ///
    /// The bundle can be either explicitly stated by adding it to the
    /// key in the following format: `bundle$key`. Alternatively, the bundle name can also be
    /// contextual retrieved by a search for the [Bundle] annotation, see class docs.
    ///
    /// @param locale      the [Locale] to be used to localize the key
    /// @param key         the messages key
    /// @param placeholder the placeholder to be used
    /// @return the localized message or the key if not found
    public String localize(Locale locale, String key, Entry... placeholder) {
        return localize(locale, key, Entry.toMap(placeholder));
    }

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
