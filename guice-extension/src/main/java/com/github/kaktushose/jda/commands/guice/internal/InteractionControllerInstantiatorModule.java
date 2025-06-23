package com.github.kaktushose.jda.commands.guice.internal;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.i18n.I18n;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class InteractionControllerInstantiatorModule extends AbstractModule {

    private final I18n i18n;
    private final JDA jda;

    public InteractionControllerInstantiatorModule(I18n i18n, JDA jda) {
        this.i18n = i18n;
        this.jda = jda;
    }

    @Override
    protected void configure() {
        bindScope(Interaction.class, new PerModuleScope());
    }

    @Provides
    public JDA jda() {
        return jda;
    }

    @Provides
    public I18n i18n() {
        return i18n;
    }
}
