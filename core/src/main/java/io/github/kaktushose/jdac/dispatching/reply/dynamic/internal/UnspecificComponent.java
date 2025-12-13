package io.github.kaktushose.jdac.dispatching.reply.dynamic.internal;

import io.github.kaktushose.jdac.definitions.interactions.component.ComponentDefinition;
import io.github.kaktushose.jdac.dispatching.reply.Component;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.components.ActionComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.Internal
public final class UnspecificComponent extends Component<UnspecificComponent, ActionComponent, ActionRowChildComponent, ComponentDefinition<ActionComponent>> {

    public UnspecificComponent(boolean enabled, boolean independent, String component, @Nullable Class<?> origin, Entry[] placeholder) {
        super(component, origin, placeholder);
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
