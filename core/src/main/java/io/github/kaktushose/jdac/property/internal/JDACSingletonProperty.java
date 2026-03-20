package io.github.kaktushose.jdac.property.internal;

import dev.goldmensch.propane.property.Property;
import dev.goldmensch.propane.property.SingleProperty;
import io.github.kaktushose.jdac.property.JDACIntrospection;
import io.github.kaktushose.jdac.property.JDACProperty;
import io.github.kaktushose.jdac.property.JDACScope;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;

public final class JDACSingletonProperty<T> extends SingleProperty<T> implements JDACProperty<T> {
  public JDACSingletonProperty(String name, Property.Source source, JDACScope scope,
      Class<T> type) {
    super(name, source, scope, type);
  }

  @Override
  public T getScoped() {
    return JDACIntrospection.scopedGet(this);
  }
}
