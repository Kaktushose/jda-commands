package io.github.kaktushose.jdac.guice.internal;

import io.github.kaktushose.jdac.annotations.interactions.Interaction;
import io.github.kaktushose.jdac.message.i18n.I18n;
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
