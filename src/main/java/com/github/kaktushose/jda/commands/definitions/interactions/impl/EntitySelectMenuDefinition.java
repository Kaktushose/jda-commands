package com.github.kaktushose.jda.commands.definitions.interactions.impl;

import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.features.Replyable;
import com.github.kaktushose.jda.commands.definitions.interactions.CustomIdInteraction;
import com.github.kaktushose.jda.commands.definitions.interactions.PermissionsInteraction;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.SequencedCollection;
import java.util.Set;

public record EntitySelectMenuDefinition(
        @NotNull Method method,
        @NotNull Collection<String> permissions,
        @NotNull Set<EntitySelectMenu.SelectTarget> selectTargets,
        @NotNull Set<EntitySelectMenu.DefaultValue> defaultValues,
        @NotNull Set<ChannelType> channelTypes,
        @NotNull String placeholder,
        int minValue,
        int maxValue
) implements JDAEntity<EntitySelectMenu>, Replyable, PermissionsInteraction, CustomIdInteraction {

    @NotNull
    @Override
    public EntitySelectMenu toJDAEntity() {
        return toSelectMenu(independentCustomId().customId());
    }

    @NotNull
    @Override
    public EntitySelectMenu toJDAEntity(@NotNull String runtimeId) {
        return toSelectMenu(boundCustomId(runtimeId).customId());
    }

    @NotNull
    private EntitySelectMenu toSelectMenu(@NotNull String customId) {
        var menu = net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.create(customId, selectTargets)
                .setDefaultValues(defaultValues)
                .setPlaceholder(placeholder)
                .setRequiredRange(minValue, maxValue);

        // ChannelType.UNKNOWN is the default value inside the annotation. if this statement is true, we can assume that
        // no channel type was selected
        channelTypes.remove(ChannelType.UNKNOWN);
        if (!channelTypes.isEmpty()) {
            menu.setChannelTypes(channelTypes);
        }
        return menu.build();
    }

    @Override
    public @NotNull String displayName() {
        return "Select Menu: %s".formatted(placeholder);
    }

    @Override
    public @NotNull SequencedCollection<Class<?>> parameters() {
        return List.of(ComponentEvent.class, Mentions.class);
    }
}
