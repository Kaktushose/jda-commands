package com.github.kaktushose.jda.commands.definitions.interactions.component.menu;

import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.interactions.CustomId;
import com.github.kaktushose.jda.commands.definitions.interactions.MethodBuildContext;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record EntitySelectMenuDefinition(
        @NotNull ClassDescription clazzDescription,
        @NotNull MethodDescription methodDescription,
        @NotNull Collection<String> permissions,
        @NotNull Set<EntitySelectMenu.SelectTarget> selectTargets,
        @NotNull Set<EntitySelectMenu.DefaultValue> defaultValues,
        @NotNull Set<ChannelType> channelTypes,
        @NotNull String placeholder,
        int minValue,
        int maxValue
) implements SelectMenuDefinition<EntitySelectMenu> {

    public static Optional<Definition> build(MethodBuildContext context) {
        var method = context.method();
        com.github.kaktushose.jda.commands.annotations.interactions.EntitySelectMenu selectMenu =
                method.annotation(com.github.kaktushose.jda.commands.annotations.interactions.EntitySelectMenu.class).orElseThrow();

        if (Helpers.checkSignature(method, List.of(ComponentEvent.class, Mentions.class))) {
            return Optional.empty();
        }

        Set<EntitySelectMenu.DefaultValue> defaultValueSet = new HashSet<>();
        for (long id : selectMenu.defaultChannels()) {
            if (id < 0) continue;
            defaultValueSet.add(EntitySelectMenu.DefaultValue.channel(id));
        }
        for (long id : selectMenu.defaultUsers()) {
            if (id < 0) continue;
            defaultValueSet.add(EntitySelectMenu.DefaultValue.user(id));
        }
        for (long id : selectMenu.defaultRoles()) {
            if (id < 0) continue;
            defaultValueSet.add(EntitySelectMenu.DefaultValue.role(id));
        }

        return Optional.of(new EntitySelectMenuDefinition(
                context.clazz(),
                method,
                Helpers.permissions(context),
                Set.of(selectMenu.value()),
                defaultValueSet,
                new HashSet<>(Set.of(selectMenu.channelTypes())),
                selectMenu.placeholder(),
                selectMenu.minValue(),
                selectMenu.maxValue()
        ));
    }

    @NotNull
    @Override
    public EntitySelectMenu toJDAEntity() {
        return toJDAEntity(CustomId.independent(definitionId()));
    }

    @NotNull
    @Override
    public EntitySelectMenu toJDAEntity(@NotNull CustomId customId) {
        var menu = net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.create(customId.id(), selectTargets)
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

}
