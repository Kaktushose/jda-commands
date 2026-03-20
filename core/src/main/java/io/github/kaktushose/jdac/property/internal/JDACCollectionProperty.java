package io.github.kaktushose.jdac.property.internal;

import dev.goldmensch.propane.property.CollectionProperty;
import dev.goldmensch.propane.property.Property;
import io.github.kaktushose.jdac.property.JDACIntrospection;
import io.github.kaktushose.jdac.property.JDACProperty;
import io.github.kaktushose.jdac.property.JDACScope;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.util.Collection;

public final class JDACCollectionProperty<T> extends CollectionProperty<T> implements JDACProperty<Collection<T>> {
  public JDACCollectionProperty(String name, Property.Source source, JDACScope scope, Class<T> type,
      Property.FallbackBehaviour fallback) {
    super(name, source, scope, type, fallback);
  }

  @Override
  public Collection<T> getScoped() {
    return JDACIntrospection.scopedGet(this);
  }
}
