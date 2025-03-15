package com.github.kaktushose.jda.commands.dispatching.reply.component;

import com.github.kaktushose.jda.commands.definitions.interactions.component.ComponentDefinition;
import com.github.kaktushose.jda.commands.dispatching.reply.Component;

public final class UnspecificComponent extends Component<UnspecificComponent, ComponentDefinition<?>> {

    public UnspecificComponent(boolean enabled, boolean independent, String component, Class<?> origin) {
        super(component, origin);
        this.enabled = enabled;
        this.independent = independent;
    }

    // works - don't touch it
    @SuppressWarnings("unchecked")
    @Override
    public Class<ComponentDefinition<?>> definitionClass() {
        return (Class<ComponentDefinition<?>>) (Class<?>) ComponentDefinition.class;
    }

    @Override
    public ComponentDefinition<?> build(ComponentDefinition<?> definition) {
        return definition;
    }
}
