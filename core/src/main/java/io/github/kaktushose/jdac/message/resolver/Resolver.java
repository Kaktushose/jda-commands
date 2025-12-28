package io.github.kaktushose.jdac.message.resolver;

import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

@FunctionalInterface
public interface Resolver<T> {

    T resolve(T object, Locale locale, Map<String, @Nullable Object> placeholders);

}
