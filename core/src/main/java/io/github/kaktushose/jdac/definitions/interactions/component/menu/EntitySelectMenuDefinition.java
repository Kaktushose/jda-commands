package io.github.kaktushose.jdac.definitions.interactions.component.menu;

import io.github.kaktushose.jdac.definitions.description.ClassDescription;
import io.github.kaktushose.jdac.definitions.description.MethodDescription;
import io.github.kaktushose.jdac.definitions.interactions.CustomId;
import io.github.kaktushose.jdac.definitions.interactions.MethodBuildContext;
import io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent;
import io.github.kaktushose.jdac.internal.Helpers;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu.DefaultValue;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu.SelectTarget;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import org.jspecify.annotations.Nullable;

import java.util.*;

import static io.github.kaktushose.jdac.definitions.interactions.component.ComponentDefinition.override;

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
/// @param uniqueId          the uniqueId of this menu
public record EntitySelectMenuDefinition(
        ClassDescription classDescription,
        MethodDescription methodDescription,
        Collection<String> permissions,
        Set<SelectTarget> selectTargets,
        Set<DefaultValue> defaultValues,
        Set<ChannelType> channelTypes,
        String placeholder,
        int minValue,
        int maxValue,
        @Nullable Integer uniqueId
) implements SelectMenuDefinition<EntitySelectMenu> {

    /// Builds a new [EntitySelectMenuDefinition] from the given [MethodBuildContext].
    ///
    /// @return an [Optional] holding the [EntitySelectMenuDefinition]
    public static EntitySelectMenuDefinition build(MethodBuildContext context) {
        var method = context.method();
        io.github.kaktushose.jdac.annotations.interactions.EntitySelectMenu selectMenu =
                method.annotation(io.github.kaktushose.jdac.annotations.interactions.EntitySelectMenu.class).orElseThrow();

        Helpers.checkSignature(method, List.of(ComponentEvent.class, Mentions.class));

        Set<DefaultValue> defaultValueSet = new HashSet<>();
        for (long id : selectMenu.defaultChannels()) {
            if (id < 0) continue;
            defaultValueSet.add(DefaultValue.channel(id));
        }
        for (long id : selectMenu.defaultUsers()) {
            if (id < 0) continue;
            defaultValueSet.add(DefaultValue.user(id));
        }
        for (long id : selectMenu.defaultRoles()) {
            if (id < 0) continue;
            defaultValueSet.add(DefaultValue.role(id));
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
                selectMenu.maxValue(),
                selectMenu.uniqueId() < 0 ? null : selectMenu.uniqueId()
        );
    }

    /// Builds a new [EntitySelectMenuDefinition] with the given values.

    public EntitySelectMenuDefinition with(@Nullable Set<SelectTarget> selectTargets,
                                           @Nullable Set<DefaultValue> defaultValues,
                                           @Nullable Set<ChannelType> channelTypes,
                                           @Nullable String placeholder,
                                           @Nullable Integer minValue,
                                           @Nullable Integer maxValue,
                                           @Nullable Integer uniqueId) {
        return new EntitySelectMenuDefinition(
                classDescription,
                methodDescription,
                permissions,
                override(HashSet::new, this.selectTargets, selectTargets),
                override(HashSet::new, this.defaultValues, defaultValues),
                override(HashSet::new, this.channelTypes, channelTypes),
                override(this.placeholder, placeholder),
                override(this.minValue, minValue),
                override(this.maxValue, maxValue),
                override(this.uniqueId, uniqueId)
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
        try {
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
            if (uniqueId != null) {
                menu.setUniqueId(uniqueId);
            }
            return menu.build();
        } catch (IllegalArgumentException e) {
            throw Helpers.jdaException(e, this);
        }
    }

    @Override
    public String displayName() {
        return "Entity Select Menu: %s".formatted(placeholder);
    }
}
