package io.github.kaktushose.jdac.configuration.internal;

import io.github.kaktushose.jdac.JDACommands;
import io.github.kaktushose.jdac.configuration.Extension;
import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.internal.logging.JDACLogger;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.ServiceLoader;

@ApiStatus.Internal
public class Extensions {
    private static final Logger log = JDACLogger.getLogger(Extensions.class);

    private final Collection<Extension<Extension.Data>> loaded = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public void load(Resolver resolver) {
        Map<Class<? extends Extension.Data>, Extension.Data> extensionData = resolver.get(Property.EXTENSION_DATA);

        ServiceLoader.load(Extension.class)
                .stream()
                .peek(provider -> log.debug("Found extension (filter not applied): {}", provider.type()))
                .filter(resolver.get(Property.EXTENSION_FILTER))
                .peek(provider -> log.debug("Using extension (filter applied): {}", provider.type()))
                .map(ServiceLoader.Provider::get)
                .peek(extension -> extension.init(extensionData.get(extension.dataType())))
                .forEach(loaded::add);
    }

    public void register(Properties properties) {
        for (Extension<Extension.Data> extension : loaded) {
            if (extension.getClass().getName().startsWith("io.github.kaktushose.jdac")) {
                ScopedValue.where(Properties.INSIDE_FRAMEWORK, true)
                        .run(() -> properties.addAll(extension.properties()));
            } else {
                properties.addAll(extension.properties());
            }
        }
    }

    public void callOnStart(JDACommands jdaCommands) {
        loaded.forEach(ext -> ext.onStart(jdaCommands));
    }
}
