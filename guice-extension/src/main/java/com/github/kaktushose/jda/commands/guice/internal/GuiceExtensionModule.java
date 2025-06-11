package com.github.kaktushose.jda.commands.guice.internal;

import com.github.kaktushose.jda.commands.guice.Implementation;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class GuiceExtensionModule extends AbstractModule {
    @Override
    protected void configure() {
        bindScope(Implementation.class, Scopes.SINGLETON);
        bindScope(Implementation.Middleware.class, Scopes.SINGLETON);
        bindScope(Implementation.TypeAdapter.class, Scopes.SINGLETON);
        bindScope(Implementation.Validator.class, Scopes.SINGLETON);
    }
}
