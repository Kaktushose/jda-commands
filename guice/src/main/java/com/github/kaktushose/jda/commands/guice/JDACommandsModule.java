package com.github.kaktushose.jda.commands.guice;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.google.inject.AbstractModule;

public class JDACommandsModule extends AbstractModule {

    @Override
    protected void configure() {
        bindScope(Interaction.class, new PerModuleScope());
    }
}
