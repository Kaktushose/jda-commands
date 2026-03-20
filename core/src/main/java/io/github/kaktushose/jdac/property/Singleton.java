package io.github.kaktushose.jdac.property;

import dev.goldmensch.propane.property.Property;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@interface Singleton {
  Property.Source source();

  JDACScope scope();
}
