package com.github.kaktushose.jda.commands.guice.internal;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.dispatching.instance.InstanceProvider;
import com.github.kaktushose.jda.commands.guice.GuiceExtension;

public class GuiceRootInstanceProvider implements InstanceProvider {

    private final GuiceExtension extension;

    public GuiceRootInstanceProvider(GuiceExtension extension) {
        this.extension = extension;
    }

    @Override
    public <T> T instance(Class<T> clazz, Context context) {
        throw new UnsupportedOperationException("GuiceInstanceProvider must be used runtime bound!");
    }

    /// Creates a new child injector with its own [JDACommandsModule] for each runtime.
    /// This has the effect, that each class annotated with [Interaction] will be treated as a runtime scoped singleton.
    @Override
    public InstanceProvider forRuntime(String id) {
        return new GuiceInstanceProvider(extension.injector().createChildInjector(new JDACommandsModule(extension.jdaCommands())));
    }
}
