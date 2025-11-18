package io.github.kaktushose.jdac.configuration.internal;

import io.github.kaktushose.jdac.configuration.PropertyTypes;
import io.github.kaktushose.jdac.extension.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.ServiceLoader;

class Extensions {
    public static final Logger log = LoggerFactory.getLogger(Extensions.class);

    private final Collection<Extension<Extension.Data>> loaded = new ArrayList<>();

    @SuppressWarnings("unchecked")
    void load(Resolver resolver) {
        Map<Class<? extends Extension.Data>, Extension.Data> extensionData = resolver.get(PropertyTypes.EXTENSION_DATA);

        ServiceLoader.load(Extension.class)
                .stream()
                .peek(provider -> log.debug("Found extension: {}", provider.type()))
                .filter(resolver.get(PropertyTypes.EXTENSION_FILTER))
                .peek(provider -> log.debug("Using extension {}", provider.type()))
                .map(ServiceLoader.Provider::get)
                .map(extension -> (Extension<Extension.Data>) extension)
                .peek(extension -> extension.init(extensionData.get(extension.dataType())))
                .forEach(loaded::add);
    }

    void register(Properties properties) {
        for (Extension<Extension.Data> extension : loaded) {
            properties.addAll(extension.properties());
        }
    }
}
