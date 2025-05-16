package com.github.kaktushose.jda.commands.guice.internal;

import com.github.kaktushose.jda.commands.guice.annotation.Implementation;
import com.github.kaktushose.jda.commands.guice.annotation.Middlewares;
import com.github.kaktushose.jda.commands.guice.annotation.TypeAdapters;
import com.github.kaktushose.jda.commands.guice.annotation.Validators;
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
