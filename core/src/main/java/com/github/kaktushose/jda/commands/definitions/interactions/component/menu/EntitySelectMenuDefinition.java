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
import org.jspecify.annotations.Nullable;

import java.util.*;

import static com.github.kaktushose.jda.commands.definitions.interactions.component.ComponentDefinition.override;

/// Representation of an entity select menu.
///
/// @param classDescription  the [ClassDescription] of the declaring class of the [#methodDescription()]
/// @param methodDescription the [MethodDescription] of the method this definition is bound to
/// @param permissions       a [Collection] of permissions for this menu
/// @param selectTargets     the [EntitySelectMenu.SelectTarget]s of this menu
/// @param defaultValues     the [EntitySelectMenu.DefaultValue]s of this menu
/// @param channelTypes      the [ChannelType]s that should be supported by this menu.
/// @param placeholder       the placeholder text of this menu
/// @param minValue          the minimum amount of choices
/// @param maxValue          the maximum amount of choices
public record EntitySelectMenuDefinition(
        ClassDescription classDescription,
        MethodDescription methodDescription,
        Collection<String> permissions,
        Set<EntitySelectMenu.SelectTarget> selectTargets,
        Set<EntitySelectMenu.DefaultValue> defaultValues,
        Set<ChannelType> channelTypes,
        String placeholder,
        int minValue,
        int maxValue
) implements SelectMenuDefinition<EntitySelectMenu> {

    /// Builds a new [EntitySelectMenuDefinition] with the given values.
    
    public EntitySelectMenuDefinition with(@Nullable Set<EntitySelectMenu.SelectTarget> selectTargets,
                                           @Nullable Set<EntitySelectMenu.DefaultValue> defaultValues,
                                           @Nullable Set<ChannelType> channelTypes,
                                           @Nullable String placeholder,
                                           @Nullable Integer minValue,
                                           @Nullable Integer maxValue) {
        return new EntitySelectMenuDefinition(
                classDescription,
                methodDescription,
                permissions,
                override(HashSet::new, this.selectTargets, selectTargets),
                override(HashSet::new, this.defaultValues, defaultValues),
                override(HashSet::new, this.channelTypes, channelTypes),
                override(this.placeholder, placeholder),
                override(this.minValue, minValue),
                override(this.maxValue, maxValue)
        );
    }

    /// Builds a new [EntitySelectMenuDefinition] from the given [MethodBuildContext].
    ///
    /// @return an [Optional] holding the [EntitySelectMenuDefinition]
    public static EntitySelectMenuDefinition build(MethodBuildContext context) {
        var method = context.method();
        com.github.kaktushose.jda.commands.annotations.interactions.EntitySelectMenu selectMenu =
                method.annotation(com.github.kaktushose.jda.commands.annotations.interactions.EntitySelectMenu.class).orElseThrow();

        Helpers.checkSignature(method, List.of(ComponentEvent.class, Mentions.class));

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

        return new EntitySelectMenuDefinition(
                context.clazz(),
                method,
                Helpers.permissions(context),
                Set.of(selectMenu.value()),
                defaultValueSet,
                new HashSet<>(Set.of(selectMenu.channelTypes())),
                selectMenu.placeholder(),
                selectMenu.minValue(),
                selectMenu.maxValue()
        );
    }

    /// Transforms this definition to an [EntitySelectMenu] with an independent custom id.
    ///
    /// @return the [EntitySelectMenu]
    /// @see CustomId#independent(String)
    
    @Override
    public EntitySelectMenu toJDAEntity() {
        return toJDAEntity(CustomId.independent(definitionId()));
    }

    /// Transforms this definition to an [EntitySelectMenu] with the given [CustomId].
    ///
    /// @param customId the [CustomId] to use
    /// @return the [EntitySelectMenu]
    
    @Override
    public EntitySelectMenu toJDAEntity(CustomId customId) {
        var menu = EntitySelectMenu.create(customId.merged(), selectTargets)
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
    public String displayName() {
        return "Select Menu: %s".formatted(placeholder);
    }

}
