package com.github.kaktushose.jda.commands.dispatching.reply.component;

import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.EntitySelectMenuDefinition;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class EntitySelectMenuComponent extends SelectMenuComponent<EntitySelectMenuComponent, EntitySelectMenu, EntitySelectMenuDefinition> {

    private Set<EntitySelectMenu.SelectTarget> entityTypes;
    private Set<ChannelType> channelTypes;
    private Set<EntitySelectMenu.DefaultValue> defaultValues;

    public EntitySelectMenuComponent(String method, Class<?> origin) {
        super(method, origin);
    }

    @NotNull
    public EntitySelectMenuComponent entityTypes(@NotNull Set<EntitySelectMenu.SelectTarget> types) {
        this.entityTypes = types;
        return this;
    }

    @NotNull
    public EntitySelectMenuComponent channelTypes(@NotNull Set<ChannelType> types) {
        this.channelTypes = types;
        return this;
    }

    @NotNull
    public EntitySelectMenuComponent defaultValues(@NotNull EntitySelectMenu.DefaultValue... values) {
        this.defaultValues = Set.of(values);
        return this;
    }

    @Override
    public Class<EntitySelectMenuDefinition> definitionClass() {
        return EntitySelectMenuDefinition.class;
    }

    @Override
    public EntitySelectMenuDefinition build(EntitySelectMenuDefinition definition) {
        return definition.with(entityTypes, defaultValues, channelTypes);
    }
}
