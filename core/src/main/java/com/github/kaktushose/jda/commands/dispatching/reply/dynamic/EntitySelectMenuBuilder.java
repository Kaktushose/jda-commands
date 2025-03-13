package com.github.kaktushose.jda.commands.dispatching.reply.dynamic;

import com.github.kaktushose.jda.commands.definitions.interactions.component.ComponentDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.EntitySelectMenuDefinition;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public final class EntitySelectMenuBuilder extends SelectMenuBuilder<EntitySelectMenu, EntitySelectMenu.Builder> {

    private final EntitySelectMenuDefinition definition;

    public EntitySelectMenuBuilder(EntitySelectMenuDefinition definition) {
        super(definition.toJDAEntity().createCopy());
        this.definition = definition;
    }

    @NotNull
    public EntitySelectMenuBuilder entityTypes(@NotNull Collection<EntitySelectMenu.SelectTarget> types) {
        menu.setEntityTypes(types);
        return this;
    }

    @NotNull
    public EntitySelectMenuBuilder entityTypes(@NotNull EntitySelectMenu.SelectTarget type, @NotNull EntitySelectMenu.SelectTarget... types) {
        menu.setEntityTypes(type, types);
        return this;
    }

    @NotNull
    public EntitySelectMenuBuilder channelTypes(@NotNull Collection<ChannelType> types) {
        menu.setChannelTypes(types);
        return this;
    }

    @NotNull
    public EntitySelectMenuBuilder channelTypes(@NotNull ChannelType... types) {
        menu.setChannelTypes(types);
        return this;
    }

    @NotNull
    public EntitySelectMenuBuilder defaultValues(@NotNull EntitySelectMenu.DefaultValue... values) {
        menu.setDefaultValues(values);
        return this;
    }

    @NotNull
    public EntitySelectMenuBuilder defaultValues(@NotNull Collection<? extends EntitySelectMenu.DefaultValue> values) {
        menu.setDefaultValues(values);
        return this;
    }

    @Override
    public EntitySelectMenuDefinition build() {
        return new EntitySelectMenuDefinition(definition, menu.build());
    }

}
