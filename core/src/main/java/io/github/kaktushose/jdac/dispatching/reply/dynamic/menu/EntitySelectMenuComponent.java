package io.github.kaktushose.jdac.dispatching.reply.dynamic.menu;

import io.github.kaktushose.jdac.definitions.interactions.component.menu.EntitySelectMenuDefinition;
import io.github.kaktushose.jdac.dispatching.reply.Component;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu.Builder;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.util.*;

import static net.dv8tion.jda.api.components.Component.Type.MENTIONABLE_SELECT;

/// An implementation of [Component] specific to [EntitySelectMenu]
public final class EntitySelectMenuComponent
        extends SelectMenuComponent<EntitySelectMenuComponent, EntitySelectMenu, Builder, EntitySelectMenuDefinition>
        implements EntitySelectMenu {

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
        return definition.with(entityTypes, defaultValues, channelTypes, placeholder, minValues, maxValues, uniqueId);
    }

    @Override
    public Type getType() {
        return MENTIONABLE_SELECT;
    }

    @Override
    public EnumSet<SelectTarget> getEntityTypes() {
        return EnumSet.of(SelectTarget.USER);
    }

    @Override
    public EnumSet<ChannelType> getChannelTypes() {
        return EnumSet.copyOf(channelTypes);
    }

    @Override
    public @Unmodifiable List<DefaultValue> getDefaultValues() {
        return defaultValues.stream().toList();
    }
}
