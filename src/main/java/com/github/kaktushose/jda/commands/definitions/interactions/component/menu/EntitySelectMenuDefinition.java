package com.github.kaktushose.jda.commands.definitions.interactions.component.menu;

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

/// Representation of an entity select menu.
///
/// @param clazzDescription  the [ClassDescription] of the declaring class of the [#methodDescription()]
/// @param methodDescription the [MethodDescription] of the method this definition is bound to
/// @param permissions       a [Collection] of permissions for this menu
/// @param selectTargets     the [EntitySelectMenu.SelectTarget]s of this menu
/// @param defaultValues     the [EntitySelectMenu.DefaultValue]s of this menu
/// @param channelTypes      the [ChannelType]s that should be supported by this menu.
/// @param placeholder       the placeholder text of this menu
/// @param minValue          the minimum amount of choices
/// @param maxValue          the maximum amount of choices
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

    /// Builds a new [EntitySelectMenuDefinition] from the given [MethodBuildContext].
    ///
    /// @return an [Optional] holding the [EntitySelectMenuDefinition]
    public static Optional<EntitySelectMenuDefinition> build(MethodBuildContext context) {
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

    /// Transforms this definition to an [EntitySelectMenu] with an independent custom id.
    ///
    /// @see CustomId#independent(String)
    /// @return the [EntitySelectMenu]
    @NotNull
    @Override
    public EntitySelectMenu toJDAEntity() {
        return toJDAEntity(CustomId.independent(definitionId()));
    }

    /// Transforms this definition to an [EntitySelectMenu] with the given [CustomId].
    ///
    /// @param customId the [CustomId] to use
    /// @return the [EntitySelectMenu]
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

    @NotNull
    @Override
    public String displayName() {
        return "Select Menu: %s".formatted(placeholder);
    }

}
