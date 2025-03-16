package com.github.kaktushose.jda.commands.dispatching.reply.component.menu;

import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.EntitySelectMenuDefinition;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class EntitySelectMenuComponent extends SelectMenuComponent<EntitySelectMenuComponent, EntitySelectMenu, EntitySelectMenuDefinition> {

    private final Set<EntitySelectMenu.SelectTarget> entityTypes = new HashSet<>();
    private final Set<ChannelType> channelTypes = new HashSet<>();
    private final Set<EntitySelectMenu.DefaultValue> defaultValues = new HashSet<>();

    public EntitySelectMenuComponent(String method, Class<?> origin) {
        super(method, origin);
    }

    @NotNull
    public EntitySelectMenuComponent entityTypes(@NotNull Set<EntitySelectMenu.SelectTarget> types) {
        this.entityTypes.addAll(types);
        return this;
    }

    @NotNull
    public EntitySelectMenuComponent entityTypes(@NotNull EntitySelectMenu.SelectTarget... types) {
        this.entityTypes.addAll(Arrays.asList(types));
        return this;
    }

    @NotNull
    public EntitySelectMenuComponent channelTypes(@NotNull Set<ChannelType> types) {
        this.channelTypes.addAll(types);
        return this;
    }

    @NotNull
    public EntitySelectMenuComponent channelTypes(@NotNull ChannelType... types) {
        this.channelTypes.addAll(Arrays.asList(types));
        return this;
    }

    @NotNull
    public EntitySelectMenuComponent defaultValues(@NotNull Set<EntitySelectMenu.DefaultValue> values) {
        this.defaultValues.addAll(values);
        return this;
    }

    @NotNull
    public EntitySelectMenuComponent defaultValues(@NotNull EntitySelectMenu.DefaultValue... values) {
        this.defaultValues.addAll(Arrays.asList(values));
        return this;
    }

    @Override
    public Class<EntitySelectMenuDefinition> definitionClass() {
        return EntitySelectMenuDefinition.class;
    }

    @Override
    public EntitySelectMenuDefinition build(EntitySelectMenuDefinition definition) {
        return definition.with(entityTypes, defaultValues, channelTypes, placeholder, minValues, maxValues);
    }
}
