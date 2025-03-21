package com.github.kaktushose.jda.commands.dispatching.reply.dynamic.internal;

import com.github.kaktushose.jda.commands.definitions.interactions.component.ComponentDefinition;
import com.github.kaktushose.jda.commands.dispatching.reply.Component;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class UnspecificComponent extends Component<UnspecificComponent, ActionComponent, ComponentDefinition<ActionComponent>> {

    public UnspecificComponent(boolean enabled, boolean independent, @NotNull String component, @Nullable Class<?> origin) {
        super(component, origin);
        enabled(enabled);
        independent(independent);
    }

    // works - don't touch it
    @SuppressWarnings("unchecked")
    @Override
    public Class<ComponentDefinition<ActionComponent>> definitionClass() {
        return (Class<ComponentDefinition<ActionComponent>>) (Class<?>) ComponentDefinition.class;
    }

    @Override
    public ComponentDefinition<ActionComponent> build(ComponentDefinition<ActionComponent> definition) {
        return definition;
    }
}
