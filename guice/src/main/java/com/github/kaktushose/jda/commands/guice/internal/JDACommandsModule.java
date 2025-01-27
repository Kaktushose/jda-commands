package com.github.kaktushose.jda.commands.guice.internal;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import net.dv8tion.jda.api.JDA;

public class JDACommandsModule extends AbstractModule {

    private final JDACommands jdaCommands;
    private final JDA jda;

    public JDACommandsModule(JDACommands jdaCommands, JDA jda) {
        this.jdaCommands = jdaCommands;
        this.jda = jda;
    }

    @Override
    protected void configure() {
        bindScope(Interaction.class, new PerModuleScope());
    }

    @Provides
    public JDACommands jdaCommands() {
        return jdaCommands;
    }

    @Provides
    public JDA jda() {
        return jda;
    }
}
