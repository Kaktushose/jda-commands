package com.github.kaktushose.jda.commands.guice.internal;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class JDACommandsModule extends AbstractModule {

    private final JDACommands jdaCommands;

    public JDACommandsModule(JDACommands jdaCommands) {
        this.jdaCommands = jdaCommands;
    }

    @Override
    protected void configure() {
        bindScope(Interaction.class, new PerModuleScope());
    }

    @Provides
    public JDACommands jdaCommands() {
        return jdaCommands;
    }
}
