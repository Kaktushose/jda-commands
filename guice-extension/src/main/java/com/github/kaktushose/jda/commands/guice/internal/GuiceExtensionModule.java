package com.github.kaktushose.jda.commands.guice.internal;

import com.github.kaktushose.jda.commands.guice.Implementation;
import com.github.kaktushose.jda.commands.guice.Middlewares;
import com.github.kaktushose.jda.commands.guice.TypeAdapters;
import com.github.kaktushose.jda.commands.guice.Validators;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class GuiceExtensionModule extends AbstractModule {
    @Override
    protected void configure() {
        bindScope(Implementation.class, Scopes.SINGLETON);
        bindScope(Middlewares.class, Scopes.SINGLETON);
        bindScope(TypeAdapters.class, Scopes.SINGLETON);
        bindScope(Validators.class, Scopes.SINGLETON);
    }
}
