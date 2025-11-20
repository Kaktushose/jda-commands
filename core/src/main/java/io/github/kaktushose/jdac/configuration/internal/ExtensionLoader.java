package io.github.kaktushose.jdac.configuration.internal;

import io.github.kaktushose.jdac.configuration.Extension;
import io.github.kaktushose.jdac.configuration.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.ServiceLoader;

class ExtensionLoader {
    public static final Logger log = LoggerFactory.getLogger(ExtensionLoader.class);

    private final Collection<Extension<Extension.Data>> loaded = new ArrayList<>();

    @SuppressWarnings("unchecked")
    void load(Resolver resolver) {
        Map<Class<? extends Extension.Data>, Extension.Data> extensionData = resolver.get(Property.EXTENSION_DATA);

        ServiceLoader.load(Extension.class)
                .stream()
                .peek(provider -> log.debug("Found extension: {}", provider.type()))
                .filter(resolver.get(InternalPropertyProviders.EXTENSION_FILTER))
                .peek(provider -> log.debug("Using extension {}", provider.type()))
                .map(ServiceLoader.Provider::get)
                .peek(extension -> extension.init(extensionData.get(extension.dataType())))
                .forEach(loaded::add);
    }

    void register(Properties properties) {
        for (Extension<Extension.Data> extension : loaded) {
            if (extension.getClass().getName().startsWith("io.github.kaktushose.jdac")) {
                ScopedValue.where(Properties.INSIDE_FRAMEWORK, true).run(() -> properties.addAll(extension.properties()));
            } else {
                properties.addAll(extension.properties());
            }
        }
    }
}
