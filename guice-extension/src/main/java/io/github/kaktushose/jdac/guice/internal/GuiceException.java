package io.github.kaktushose.jdac.guice.internal;

import io.github.kaktushose.jdac.configuration.ExtensionException;
import io.github.kaktushose.jdac.message.placeholder.Entry;

public final class GuiceException extends ExtensionException {

    private static final ExtensionException.Information INFORMATION = new Information("jdac_guice");

    public GuiceException(String key, Entry... placeholder) {
        super(INFORMATION, key, placeholder);
    }
}
