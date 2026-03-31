package io.github.kaktushose.jdac.property.internal;

import dev.goldmensch.propane.property.MappingProperty;
import dev.goldmensch.propane.property.Property;
import io.github.kaktushose.jdac.property.JDACIntrospection;
import io.github.kaktushose.jdac.property.JDACProperty;
import io.github.kaktushose.jdac.property.JDACScope;

import java.util.Map;

public final class JDACMappingProperty<K, V> extends MappingProperty<K, V> implements JDACProperty<Map<K, V>> {
    public JDACMappingProperty(String name, Property.Source source, JDACScope scope, Class<K> keyType,
                               Class<V> valueType, Property.FallbackStrategy fallback) {
        super(name, source, scope, keyType, valueType, fallback);
    }

    @Override
    public Map<K, V> getScoped() {
        return JDACIntrospection.scopedGet(this);
    }
}
