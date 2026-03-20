package io.github.kaktushose.jdac.property.internal;

import dev.goldmensch.propane.property.MapProperty;
import dev.goldmensch.propane.property.Property;
import io.github.kaktushose.jdac.property.JDACIntrospection;
import io.github.kaktushose.jdac.property.JDACProperty;
import io.github.kaktushose.jdac.property.JDACScope;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.util.Map;

public final class JDACMappingProperty<K, V> extends MapProperty<K, V> implements JDACProperty<Map<K, V>> {
  public JDACMappingProperty(String name, Property.Source source, JDACScope scope, Class<K> keyType,
      Class<V> valueType, Property.FallbackBehaviour fallback) {
    super(name, source, scope, keyType, valueType, fallback);
  }

  @Override
  public Map<K, V> getScoped() {
    return JDACIntrospection.scopedGet(this);
  }
}
