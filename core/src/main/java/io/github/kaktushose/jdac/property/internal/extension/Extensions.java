package io.github.kaktushose.jdac.property.internal.extension;

import dev.goldmensch.propane.property.Priority;
import io.github.kaktushose.jdac.JDACommands;
import io.github.kaktushose.jdac.exceptions.ConfigurationException;
import io.github.kaktushose.jdac.internal.logging.JDACLogger;
import io.github.kaktushose.jdac.property.JDACIntrospection;
import io.github.kaktushose.jdac.property.JDACProperty;
import io.github.kaktushose.jdac.property.JDACPropertyProvider;
import io.github.kaktushose.jdac.property.extension.Extension;
import io.github.kaktushose.jdac.property.internal.JDACIntrospectionImpl;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.ServiceLoader;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

@ApiStatus.Internal
public class Extensions {
    private static final Logger log = JDACLogger.getLogger(Extensions.class);

    private final Collection<Extension<Extension.Data>> loaded = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public void load(JDACIntrospection introspection) {
        Map<Class<? extends Extension.Data>, Extension.Data> extensionData = introspection.get(JDACProperty.EXTENSION_DATA);

        ServiceLoader.load(Extension.class)
                .stream()
                .peek(provider -> log.debug("Found extension (filter not applied): {}", provider.type()))
                .filter(introspection.get(JDACProperty.EXTENSION_FILTER))
                .peek(provider -> log.debug("Using extension (filter applied): {}", provider.type()))
                .map(ServiceLoader.Provider::get)
                .peek(extension -> extension.init(extensionData.get(extension.dataType())))
                .forEach(loaded::add);
    }

    public void register(JDACIntrospectionImpl.Builder builder) {
        for (Extension<Extension.Data> extension : loaded) {

            if (!extension.getClass().getName().startsWith("io.github.kaktushose.jdac")) {
                for (JDACPropertyProvider<?> provider : extension.properties()) {
                    int ordinal = provider.priority().ordinal();
                    if (ordinal >= 1 && ordinal <= 100 || provider.priority().equals(Priority.BUILDER)) {
                        throw new ConfigurationException("reserved-priority", entry("priority", ordinal));
                    }
                }
            }

            extension.properties().forEach(builder::add);
        }
    }

    public void callOnStart(JDACommands jdaCommands) {
        loaded.forEach(ext -> ext.onStart(jdaCommands));
    }
}
