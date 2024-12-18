package com.github.kaktushose.jda.commands.reflect.interactions.components.menus;

import com.github.kaktushose.jda.commands.Helpers;
import com.github.kaktushose.jda.commands.annotations.interactions.EntitySelectMenu;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.reflect.MethodBuildContext;
import com.github.kaktushose.jda.commands.reflect.interactions.ReplyConfig;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.DefaultValue;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.SelectTarget;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Representation of a {@link net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu EntitySelectMenu}.
 *
 * @see EntitySelectMenu
 * @since 4.0.0
 */
public final class EntitySelectMenuDefinition extends GenericSelectMenuDefinition<net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu> {

    private final Set<SelectTarget> selectTargets;
    private final Set<DefaultValue> defaultValues;
    private final Set<ChannelType> channelTypes;

    private EntitySelectMenuDefinition(Method method,
                                       Set<String> permissions,
                                       ReplyConfig replyConfig,
                                       Set<SelectTarget> selectTargets,
                                       Set<DefaultValue> defaultValues,
                                       Set<ChannelType> channelTypes,
                                       String placeholder,
                                       int minValue,
                                       int maxValue) {
        super(method, permissions, replyConfig, placeholder, minValue, maxValue);
        this.selectTargets = selectTargets;
        this.defaultValues = defaultValues;
        this.channelTypes = channelTypes;
    }

    /**
     * Builds a new EntitySelectMenuDefinition.
     *
     * @return an {@link Optional} holding the EntitySelectMenuDefinition
     */
    public static Optional<EntitySelectMenuDefinition> build(MethodBuildContext context) {
        Method method = context.method();
        if (!method.isAnnotationPresent(EntitySelectMenu.class) || !method.getDeclaringClass().isAnnotationPresent(Interaction.class)) {
            return Optional.empty();
        }

        if (Helpers.isIncorrectParameterAmount(method, 2)) {
            return Optional.empty();
        }

        if (Helpers.isIncorrectParameterType(method, 0, ComponentEvent.class) ||
                Helpers.isIncorrectParameterType(method, 1, List.class)) {
            return Optional.empty();
        }

        EntitySelectMenu selectMenu = method.getAnnotation(EntitySelectMenu.class);

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

        return Optional.of(new EntitySelectMenuDefinition(
                method,
                Helpers.permissions(context),
                Helpers.replyConfig(method),
                Set.of(selectMenu.value()),
                defaultValueSet,
                new HashSet<>(Set.of(selectMenu.channelTypes())),
                selectMenu.placeholder(),
                selectMenu.minValue(),
                selectMenu.maxValue()
        ));
    }

    @Override
    public net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu toSelectMenu(String runtimeId, boolean enabled) {
        var menu = net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.create(createCustomId(runtimeId), selectTargets)
                .setDefaultValues(defaultValues)
                .setPlaceholder(placeholder)
                .setRequiredRange(minValue, maxValue)
                .setDisabled(!enabled);

        // ChannelType.UNKNOWN is the default value inside the annotation. if this statement is true, we can assume that
        // no channel type was selected
        channelTypes.remove(ChannelType.UNKNOWN);
        if (!channelTypes.isEmpty()) {
            menu.setChannelTypes(channelTypes);
        }
        return menu.build();
    }

    /**
     * Gets a set of the {@link SelectTarget SelectTargets}.
     *
     * @return a set of the {@link SelectTarget SelectTargets}
     */
    public Set<SelectTarget> getSelectTargets() {
        return selectTargets;
    }

    /**
     * Gets a set of the {@link DefaultValue DefaultValues}.
     *
     * @return a set of the {@link DefaultValue DefaultValues}
     */
    public Set<DefaultValue> getDefaultValues() {
        return defaultValues;
    }

    /**
     * Gets a set of the {@link ChannelType ChannelTypes}.
     *
     * @return a set of the {@link ChannelType ChannelTypes}
     */
    public Set<ChannelType> getChannelTypes() {
        return channelTypes;
    }

    @Override
    public String toString() {
        return "EntitySelectMenuDefinition{" +
                "selectTargets=" + selectTargets +
                ", defaultValues=" + defaultValues +
                ", channelTypes=" + channelTypes +
                ", placeholder='" + placeholder + '\'' +
                ", minValue=" + minValue +
                ", maxValue=" + maxValue +
                ", permissions=" + permissions +
                ", replyConfig=" + replyConfig +
                ", id='" + definitionId + '\'' +
                ", method=" + method +
                '}';
    }
}
