package io.github.kaktushose.jdac.property.internal;

import dev.goldmensch.propane.IntrospectionImpl;
import dev.goldmensch.propane.internal.exposed.Properties;
import dev.goldmensch.propane.property.PropertyProvider;
import io.github.kaktushose.jdac.property.JDACIntrospection;
import io.github.kaktushose.jdac.property.JDACProperty;
import io.github.kaktushose.jdac.property.JDACPropertyProvider;
import io.github.kaktushose.jdac.property.JDACScope;
import java.lang.Class;
import java.lang.Override;
import java.lang.ScopedValue;
import java.util.function.Function;

public final class JDACIntrospectionImpl extends IntrospectionImpl<JDACIntrospectionImpl, JDACIntrospection, JDACIntrospectionImpl.Builder, JDACScope> implements JDACIntrospection {
  public static final ScopedValue<JDACIntrospectionImpl> INTROSPECTION = ScopedValue.newInstance();

  private JDACIntrospectionImpl(JDACScope scope, Properties<JDACIntrospection> properties,
      JDACIntrospectionImpl parent) {
    super(scope, properties, parent);
  }

  private JDACIntrospectionImpl(JDACScope scope) {
    super(Registry.INSTANCE, scope);
  }

  public static Builder create(JDACScope scope) {
    return new JDACIntrospectionImpl(scope).createChild(scope);
  }

  @Override
  public Builder createChild(JDACScope scope) {
    return this.new Builder(scope);
  }

  public <T> T get(JDACProperty<T> specific) {
    return super.get(specific);
  }

  protected void addIntrospectionProvider(Properties<JDACIntrospection> properties) {
    properties.add(new JDACPropertyProvider<>(JDACProperty.INTROSPECTION, PropertyProvider.Priority.FALLBACK, JDACIntrospection.class, _ -> this));
  }

  public final class Builder extends IntrospectionImpl<JDACIntrospectionImpl, JDACIntrospection, Builder, JDACScope>.Builder {
    Builder(JDACScope scope) {
      super(scope);
    }

    @Override
    protected JDACIntrospectionImpl newInstance() {
      return new JDACIntrospectionImpl(scope, properties, JDACIntrospectionImpl.this);
    }

    public <T> Builder addFallback(JDACProperty<T> property,
        Function<JDACIntrospection, T> supplier) {
      return add(new JDACPropertyProvider<>(property, PropertyProvider.Priority.FALLBACK, caller(), supplier));
    }

    public <T> Builder addBuilder(JDACProperty<T> property,
        Function<JDACIntrospection, T> supplier) {
      return add(new JDACPropertyProvider<>(property, PropertyProvider.Priority.BUILDER, caller(), supplier));
    }

    public <T> Builder addFallback(JDACProperty<T> property, Class<?> owner,
        Function<JDACIntrospection, T> supplier) {
      return add(new JDACPropertyProvider<>(property, PropertyProvider.Priority.FALLBACK, owner, supplier));
    }

    public <T> Builder addBuilder(JDACProperty<T> property, Class<?> owner,
        Function<JDACIntrospection, T> supplier) {
      return add(new JDACPropertyProvider<>(property, PropertyProvider.Priority.BUILDER, owner, supplier));
    }
  }
}
