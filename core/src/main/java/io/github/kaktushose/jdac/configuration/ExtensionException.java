package io.github.kaktushose.jdac.configuration;

import io.github.kaktushose.jdac.exceptions.ConfigurationException;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import dev.goldmensch.fluava.Bundle;
import dev.goldmensch.fluava.Fluava;

import java.util.Locale;

/// A sub exception of [ConfigurationException] that can be implemented by users of the library for throwing
/// own exceptions during the initialization of [Extension]s or in [PropertyProvider#supplier()].
///
/// This class comes particularly handy, because it allows to easily use exception messages defined via
/// fluent localization files in your resource folder, avoiding long strings in your source code and enabling
/// accessing all the comfortable features of the [Fluava] library.
///
/// To use this class, you have to first create an own class _extending_ this one:
/// ```java
/// public class MyException extends ExtensionException {
///     ...
/// }
/// ```
///
/// To state the [bundle][Bundle] name from which your messages should be loaded, you have to pass an instance of
/// [Information] to all the constructors you need:
///
/// ```java
/// public class MyException extends ExtensionException {
///     private static final Information INFO = new Information("extension_errors")
///
///     public MyException(String key) {
///         super(INFO, key);
///     }
///
///     public MyException(String key, Entry... placeholder) {
///         super(INFO, key, placeholder);
///     }
///
///     ...
/// }
/// ```
///
/// Now, let's assume you have the following bundle in your resource folder:
///
/// _extension_errors_en.ftl
/// ```
/// first-error = This is the first error with msg: { $msg }
/// ```
///
/// To create an exception with `first-error` as your message, just call the constructor with
/// the key `first-error` and your placeholder set:
/// `new MyException("first-error", Entry.entry("msg", "Exception in..."))`.
///
/// [Exception#getMessage()] will now be set to:
/// `This is the first error with msg: Exception in...`
///
/// Please note here, that all messages will be resolved for [Locale#ENGLISH], thus you have to provide
/// resource files for your bundle name and the english locale (they have to end on __en.ftl_ like the example above).
///
/// @see Bundle
/// @see Fluava
/// @see Entry
public abstract non-sealed class ExtensionException extends ConfigurationException {

    private final Information information;

    /// @param information the [Information] object holding all necessary (static) information to resolve the messages
    /// @param key         the bundle key of the error message
    public ExtensionException(Information information, String key) {
        super(key);
        this.information = information;
        super.bundle = Fluava.create(Locale.ENGLISH).loadBundle(information.bundleName);
    }

    /// @param information the [Information] object holding all necessary (static) information to resolve the messages
    /// @param key         the bundle key of the error message
    /// @param placeholder the placeholders to insert
    public ExtensionException(Information information, String key, Entry... placeholder) {
        super(key, placeholder);
        this.information = information;
        super.bundle = Fluava.create(Locale.ENGLISH).loadBundle(information.bundleName);
    }

    /// @param information the [Information] object holding all necessary (static) information to resolve the messages
    /// @param key         the bundle key of the error message
    /// @param cause       the cause of the exception
    public ExtensionException(Information information, String key, Throwable cause) {
        super(key, cause);
        this.information = information;
        super.bundle = Fluava.create(Locale.ENGLISH).loadBundle(information.bundleName);
    }

    /// @param information the [Information] object holding all necessary (static) information to resolve the messages
    /// @param key         the bundle key of the error message
    /// @param placeholder the placeholders to insert
    /// @param cause       the cause of the exception
    public ExtensionException(Information information, String key, Throwable cause, Entry... placeholder) {
        super(key, cause, placeholder);
        this.information = information;
        super.bundle = Fluava.create(Locale.ENGLISH).loadBundle(information.bundleName);
    }

    /// @return the stored [Information] object
    public Information information() {
        return information;
    }

    /// @return the underlying [Bundle], which is used to resolve the requested messages
    public Bundle bundle() {
        return bundle;
    }

    /// The information object holding information like the [Bundle] name to use.
    public record Information(String bundleName) { }
}
