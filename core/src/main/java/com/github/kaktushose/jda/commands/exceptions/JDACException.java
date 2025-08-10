package com.github.kaktushose.jda.commands.exceptions;

import com.github.kaktushose.jda.commands.i18n.I18n;
import dev.goldmensch.fluava.Bundle;
import dev.goldmensch.fluava.Fluava;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public sealed class JDACException extends RuntimeException
        permits ConfigurationException, InternalException, InvalidDeclarationException {

    public static final Bundle errorMessages = new Fluava(Locale.ENGLISH).loadBundle("jdac");

    public JDACException(String key) {
        super(errorMessages.message(Locale.ENGLISH, key).apply(Map.of()));
    }

    public JDACException(String key, I18n.Entry... placeholder) {
        super(errorMessages.message(Locale.ENGLISH, key).apply(
                Arrays.stream(placeholder).collect(Collectors.toUnmodifiableMap(I18n.Entry::name, I18n.Entry::value))
        ));
    }

    public JDACException(String key, Throwable cause) {
        super(errorMessages.message(Locale.ENGLISH, key).apply(Map.of()), cause);
    }
}