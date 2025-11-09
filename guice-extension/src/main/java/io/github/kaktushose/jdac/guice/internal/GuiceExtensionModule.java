package io.github.kaktushose.jdac.guice.internal;

import io.github.kaktushose.jdac.guice.Implementation;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class GuiceExtensionModule extends AbstractModule {
    @Override
    protected void configure() {
        bindScope(Implementation.class, Scopes.SINGLETON);
        bindScope(Implementation.Middleware.class, Scopes.SINGLETON);
        bindScope(Implementation.TypeAdapter.class, Scopes.SINGLETON);
        bindScope(Implementation.Validator.class, Scopes.SINGLETON);
    }
}
