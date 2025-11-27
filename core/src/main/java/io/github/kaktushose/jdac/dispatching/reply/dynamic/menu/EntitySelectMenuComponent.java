package io.github.kaktushose.jdac.dispatching.reply.dynamic.menu;

import io.github.kaktushose.jdac.definitions.interactions.component.menu.EntitySelectMenuDefinition;
import io.github.kaktushose.jdac.dispatching.reply.Component;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu.Builder;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu.DefaultValue;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu.SelectTarget;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/// An implementation of [Component] specific to [EntitySelectMenu]
public final class EntitySelectMenuComponent extends SelectMenuComponent<EntitySelectMenuComponent, EntitySelectMenu, Builder, EntitySelectMenuDefinition> {

    private final Set<SelectTarget> entityTypes = new HashSet<>();
    private final Set<ChannelType> channelTypes = new HashSet<>();
    private final Set<DefaultValue> defaultValues = new HashSet<>();

    public EntitySelectMenuComponent(String method, @Nullable Class<?> origin, Entry[] placeholder) {
        super(method, origin, placeholder);
    }

    /// @see EntitySelectMenu.Builder#setEntityTypes(Collection)
    public EntitySelectMenuComponent entityTypes(Set<SelectTarget> types) {
        this.entityTypes.addAll(types);
        return this;
    }

    /// @see EntitySelectMenu.Builder#setEntityTypes(EntitySelectMenu.SelectTarget, EntitySelectMenu.SelectTarget...)
    public EntitySelectMenuComponent entityTypes(SelectTarget... types) {
        this.entityTypes.addAll(Arrays.asList(types));
        return this;
    }

    /// @see EntitySelectMenu.Builder#setChannelTypes(Collection)
    public EntitySelectMenuComponent channelTypes(Set<ChannelType> types) {
        this.channelTypes.addAll(types);
        return this;
    }

    /// @see EntitySelectMenu.Builder#setChannelTypes(ChannelType...)
    public EntitySelectMenuComponent channelTypes(ChannelType... types) {
        this.channelTypes.addAll(Arrays.asList(types));
        return this;
    }

    /// @see EntitySelectMenu.Builder#setDefaultValues(Collection)
    public EntitySelectMenuComponent defaultValues(Collection<DefaultValue> values) {
        this.defaultValues.addAll(values);
        return this;
    }

    /// @see EntitySelectMenu.Builder#setDefaultValues(EntitySelectMenu.DefaultValue...)
    public EntitySelectMenuComponent defaultValues(DefaultValue... values) {
        this.defaultValues.addAll(Arrays.asList(values));
        return this;
    }

    @Override
    protected Class<EntitySelectMenuDefinition> definitionClass() {
        return EntitySelectMenuDefinition.class;
    }

    @Override
    protected EntitySelectMenuDefinition build(EntitySelectMenuDefinition definition) {
        return definition.with(entityTypes, defaultValues, channelTypes, placeholder, minValues, maxValues);
    }
}
