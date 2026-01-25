package io.github.kaktushose.jdac.message.i18n;

import dev.goldmensch.fluava.Fluava;
import io.github.kaktushose.jdac.annotations.i18n.Bundle;
import io.github.kaktushose.jdac.exceptions.InternalException;
import io.github.kaktushose.jdac.message.i18n.internal.BundleFinder;
import io.github.kaktushose.jdac.message.i18n.internal.JDACLocalizationFunction;
import io.github.kaktushose.jdac.message.resolver.Resolver;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;


/// This class serves as an interface for application localization.
///
/// It is mostly a wrapper around [Localizer] but supports flexible specification of the bundle to be used.
///
/// To state which bundle to use the direct way is to include it in the key following the format `bundle$key`.
/// For example a message with key `user$not-found` will be searched for in the bundle `user` and the key `not-found`.
///
/// ## dollar sign
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
/// ## Special bundle names
/// JDA-Commands uses a special bundle called 'jdac' to allow the customization of certain error messages and general strings
/// used by the framework that are presented to the user of the discord bot later. That means:
/// The bundle name 'jdac' is reserved by JDA-Commands and cannot be used for your own localization messages.
///
/// For information on what strings are localizable/customizable please visit our wiki.
///
/// ## bundle name traversal
/// If no bundle is specified, it will traverse the stack (the called methods) and search for the nearest
/// [`@Bundle("mybundle")`](Bundle) annotation with following order:
///
///
/// 1. method that called [I18n#resolve(String,Locale,Map)]
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
/// If [I18n#resolve(String, Locale, Map)]
/// would be called in, for example, `B$bTwo` the bundle would be `mB_bundle`.
public class I18n implements Resolver<String> {

    public static final String DEFAULT_BUNDLE = "default";
    private final String JDAC_BUNDLE = "jdac";
    private final FluavaLocalizer defaultsLocalizer = new FluavaLocalizer(Fluava.create(Locale.ENGLISH));

    private final BundleFinder bundleFinder;
    private final Localizer localizer;


    /// @param bundleFinder the [BundleFinder] to be used to get the [Bundle] annotation
    /// @param localizer the used [Localizer] to retrieve the messages
    public I18n(BundleFinder bundleFinder, Localizer localizer) {
        this.localizer = localizer;
        this.bundleFinder = bundleFinder;
    }

    /// This method returns the localized method found by the provided [Locale] and key
    /// in the given bundle.
    ///
    /// The bundle can be either explicitly stated by adding it to the
    /// key in the following format: `bundle$key`. Alternatively, the bundle name can also be
    /// contextual retrieved by a search for the [Bundle] annotation, see class docs.
    ///
    /// Please note that the character `$` is forbidden in bundle names and the bundle name 'jdac' is reserved.
    /// For further information visit the class docs.
    ///
    /// @param locale the [Locale] to be used to localize the key
    /// @param combinedKey the messages key
    /// @param placeholder the placeholder to be used
    ///
    /// @return the localized message or the key if not found
    @Override
    public String resolve(String combinedKey, Locale locale, Map<String, @Nullable Object> placeholder) {
        String[] bundleSplit = combinedKey.split("\\$", 2);
        String bundle = bundleSplit.length == 2 && !bundleSplit[0].isEmpty()
                ? bundleSplit[0].trim()
                : bundleFinder.findBundle();

        String key = bundleSplit.length == 2
                ? bundleSplit[1]
                : bundleSplit[0];

        if (bundle.equals(JDAC_BUNDLE)) {
            return localizer.localize(locale, JDAC_BUNDLE, key, placeholder)
                    .or(() -> defaultsLocalizer.localize(locale, JDAC_BUNDLE + "_default", key, placeholder))
                    .orElseThrow(() -> new InternalException("default-msg-not-in-bundle", entry("key", key)));
        }

        return (JDACLocalizationFunction.JDA_LOCALIZATION.orElse(false)
                ? localizer.localizeJDA(locale, bundle, key, placeholder)
                : localizer.localize(locale, bundle, key, placeholder)).orElse(combinedKey);
    }

    /// @return 2000
    @Override
    public int priority() {
        return 2000;
    }
}
