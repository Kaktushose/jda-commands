package com.github.kaktushose.jda.commands.guice.internal;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.dispatching.instance.InteractionControllerInstantiator;
import com.github.kaktushose.jda.commands.i18n.I18n;
import com.google.inject.Injector;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class GuiceInteractionControllerInstantiator implements InteractionControllerInstantiator {

    private final I18n i18n;
    private final Injector injector;

    public GuiceInteractionControllerInstantiator(I18n i18n, Injector injector) {
        this.i18n = i18n;
        this.injector = injector;
    }

    @Override
    public <T> T instance(Class<T> clazz, Context context) {
        throw new UnsupportedOperationException("GuiceInteractionControllerInstantiator must be used runtime bound!");
    }

    /// Creates a new child injector with its own [InteractionControllerInstantiatorModule] for each runtime.
    /// This has the effect, that each class annotated with [Interaction] will be treated as a runtime scoped singleton.
    @Override
    public InteractionControllerInstantiator forRuntime(String id, JDA jda) {
        Injector childInjector = injector.createChildInjector(new InteractionControllerInstantiatorModule(i18n, jda));

        return new InteractionControllerInstantiator() {
            @Override
            public <T> T instance(Class<T> clazz, Context context) {
                return childInjector.getInstance(clazz);
            }
        };
    }
}
