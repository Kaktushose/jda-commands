package io.github.kaktushose.jdac.property.internal;

import dev.goldmensch.propane.event.Event;
import io.github.kaktushose.jdac.property.JDACScope;
import java.lang.Class;
import java.util.Map;

final class Registry extends dev.goldmensch.propane.Registry<JDACScope> {
  private static final Map<Class<? extends Event<JDACScope>>, JDACScope> eventScopes = Map.ofEntries();
  ;

  static final Registry INSTANCE = new Registry();

  Registry() {
    super(eventScopes);
  }
}
