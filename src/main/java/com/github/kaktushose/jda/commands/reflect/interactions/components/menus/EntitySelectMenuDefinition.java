package com.github.kaktushose.jda.commands.reflect.interactions.components.menus;

import com.github.kaktushose.jda.commands.annotations.interactions.EntitySelectMenu;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.dispatching.interactions.components.ComponentEvent;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.DefaultValue;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.SelectTarget;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Representation of a {@link net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu EntitySelectMenu}.
 *
 * @see EntitySelectMenu
 * @since 4.0.0
 */
public class EntitySelectMenuDefinition extends GenericSelectMenuDefinition<net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu> {

    private final Set<SelectTarget> selectTargets;
    private final Set<DefaultValue> defaultValues;
    private final Set<ChannelType> channelTypes;

    protected EntitySelectMenuDefinition(Method method,
                                         Set<String> permissions,
                                         boolean ephemeral,
                                         Set<SelectTarget> selectTargets,
                                         Set<DefaultValue> defaultValues,
                                         Set<ChannelType> channelTypes,
                                         String placeholder,
                                         int minValue,
                                         int maxValue) {
        super(method, permissions, ephemeral, placeholder, minValue, maxValue);
        this.selectTargets = selectTargets;
        this.defaultValues = defaultValues;
        this.channelTypes = channelTypes;
    }

    /**
     * Builds a new EntitySelectMenuDefinition.
     *
     * @param method the {@link Method} of the button
     * @return an {@link Optional} holding the EntitySelectMenuDefinition
     */
    public static Optional<EntitySelectMenuDefinition> build(@NotNull Method method) {
        if (!method.isAnnotationPresent(EntitySelectMenu.class) || !method.getDeclaringClass().isAnnotationPresent(Interaction.class)) {
            return Optional.empty();
        }

        if (method.getParameters().length != 2) {
            log.error("An error has occurred! Skipping Button {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException("Invalid amount of parameters!"));
            return Optional.empty();
        }

        if (!ComponentEvent.class.isAssignableFrom(method.getParameters()[0].getType()) &&
                !List.class.isAssignableFrom(method.getParameters()[1].getType())) {
            log.error("An error has occurred! Skipping Button {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException(String.format("First parameter must be of type %s, second parameter of type %s!",
                            ComponentEvent.class.getSimpleName(),
                            List.class.getSimpleName()
                    )));
            return Optional.empty();
        }

        Set<String> permissions = new HashSet<>();
        if (method.isAnnotationPresent(Permissions.class)) {
            Permissions permission = method.getAnnotation(Permissions.class);
            permissions = new HashSet<>(Arrays.asList(permission.value()));
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
                permissions,
                selectMenu.ephemeral(),
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
               ", ephemeral=" + ephemeral +
               ", id='" + definitionId + '\'' +
               ", method=" + method +
               '}';
    }
}
