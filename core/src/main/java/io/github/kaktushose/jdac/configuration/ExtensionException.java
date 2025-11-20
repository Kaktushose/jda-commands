package io.github.kaktushose.jdac.configuration;

import dev.goldmensch.fluava.Bundle;
import dev.goldmensch.fluava.Fluava;
import io.github.kaktushose.jdac.exceptions.ConfigurationException;
import io.github.kaktushose.jdac.message.placeholder.Entry;

import java.util.Locale;

public abstract non-sealed class ExtensionException extends ConfigurationException {

    private final Bundle bundle;
    private final Information information;

    /// @param key the bundle key of the error message
    public ExtensionException(Information information, String key) {
        super(key);
        this.information = information;
        this.bundle = Fluava.create(Locale.ENGLISH).loadBundle(information.bundleName);
    }

    /// @param key         the bundle key of the error message
    /// @param placeholder the placeholders to insert
    public ExtensionException(Information information, String key, Entry... placeholder) {
        super(key, placeholder);
        this.information = information;
        this.bundle = Fluava.create(Locale.ENGLISH).loadBundle(information.bundleName);
    }

    /// @param key   the bundle key of the error message
    /// @param cause the cause of the internal exception
    public ExtensionException(Information information, String key, Throwable cause) {
        super(key, cause);
        this.information = information;
        this.bundle = Fluava.create(Locale.ENGLISH).loadBundle(information.bundleName);
    }

    public ExtensionException(Information information, String key, Throwable cause, Entry... placeholder) {
        super(key, cause, placeholder);
        this.information = information;
        this.bundle = Fluava.create(Locale.ENGLISH).loadBundle(information.bundleName);
    }

    public record Information(String bundleName) {}

    public Information information() {
        return information;
    }

    public Bundle bundle() {
        return bundle;
    }
}