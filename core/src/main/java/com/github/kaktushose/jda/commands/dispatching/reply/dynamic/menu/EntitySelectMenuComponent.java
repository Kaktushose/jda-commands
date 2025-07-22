package com.github.kaktushose.jda.commands.dispatching.reply.dynamic.menu;

import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.EntitySelectMenuDefinition;
import com.github.kaktushose.jda.commands.dispatching.reply.Component;
import com.github.kaktushose.jda.commands.i18n.I18n;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/// An implementation of [Component] specific to [EntitySelectMenu]
public final class EntitySelectMenuComponent extends SelectMenuComponent<EntitySelectMenuComponent, EntitySelectMenu, EntitySelectMenu.Builder, EntitySelectMenuDefinition> {

    private final Set<EntitySelectMenu.SelectTarget> entityTypes = new HashSet<>();
    private final Set<ChannelType> channelTypes = new HashSet<>();
    private final Set<EntitySelectMenu.DefaultValue> defaultValues = new HashSet<>();

    public EntitySelectMenuComponent(@NotNull String method, @Nullable Class<?> origin, I18n.Entry[] placeholder) {
        super(method, origin, placeholder);
    }

    /// @see EntitySelectMenu.Builder#setEntityTypes(Collection)
    @NotNull
    public EntitySelectMenuComponent entityTypes(@NotNull Set<EntitySelectMenu.SelectTarget> types) {
        this.entityTypes.addAll(types);
        return this;
    }

    /// @see EntitySelectMenu.Builder#setEntityTypes(EntitySelectMenu.SelectTarget, EntitySelectMenu.SelectTarget...)
    @NotNull
    public EntitySelectMenuComponent entityTypes(@NotNull EntitySelectMenu.SelectTarget... types) {
        this.entityTypes.addAll(Arrays.asList(types));
        return this;
    }

    /// @see EntitySelectMenu.Builder#setChannelTypes(Collection)
    @NotNull
    public EntitySelectMenuComponent channelTypes(@NotNull Set<ChannelType> types) {
        this.channelTypes.addAll(types);
        return this;
    }

    /// @see EntitySelectMenu.Builder#setChannelTypes(ChannelType...)
    @NotNull
    public EntitySelectMenuComponent channelTypes(@NotNull ChannelType... types) {
        this.channelTypes.addAll(Arrays.asList(types));
        return this;
    }

    /// @see EntitySelectMenu.Builder#setDefaultValues(Collection)
    @NotNull
    public EntitySelectMenuComponent defaultValues(@NotNull Collection<EntitySelectMenu.DefaultValue> values) {
        this.defaultValues.addAll(values);
        return this;
    }

    /// @see EntitySelectMenu.Builder#setDefaultValues(EntitySelectMenu.DefaultValue...)
    @NotNull
    public EntitySelectMenuComponent defaultValues(@NotNull EntitySelectMenu.DefaultValue... values) {
        this.defaultValues.addAll(Arrays.asList(values));
        return this;
    }

    @Override
    protected Class<EntitySelectMenuDefinition> definitionClass() {
        return EntitySelectMenuDefinition.class;
    }

    @Override
    protected EntitySelectMenuDefinition build(@NotNull EntitySelectMenuDefinition definition) {
        return definition.with(entityTypes, defaultValues, channelTypes, placeholder, minValues, maxValues);
    }
}
