package com.github.kaktushose.jda.commands.reflect.interactions.menus;

import com.github.kaktushose.jda.commands.annotations.interactions.EntitySelectMenu;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.commands.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.menus.SelectMenuEvent;
import com.github.kaktushose.jda.commands.reflect.interactions.EphemeralInteraction;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.DefaultValue;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.SelectTarget;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Representation of a {@link net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu EntitySelectMenu}.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @see EntitySelectMenu
 * @since 4.0.0
 */
public class EntitySelectMenuDefinition extends EphemeralInteraction {

    private final Set<SelectTarget> selectTargets;
    private final Set<DefaultValue> defaultValues;
    private final Set<ChannelType> channelTypes;
    private final String placeholder;
    private final int minValue;
    private final int maxValue;

    protected EntitySelectMenuDefinition(Method method,
                                         boolean ephemeral,
                                         Set<SelectTarget> selectTargets,
                                         Set<DefaultValue> defaultValues,
                                         Set<ChannelType> channelTypes,
                                         String placeholder,
                                         int minValue,
                                         int maxValue) {
        super(method, ephemeral);
        this.selectTargets = selectTargets;
        this.defaultValues = defaultValues;
        this.channelTypes = channelTypes;
        this.placeholder = placeholder;
        this.minValue = minValue;
        this.maxValue = maxValue;
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

        if (method.getParameters().length != 1) {
            log.error("An error has occurred! Skipping Button {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException("Invalid amount of parameters!"));
            return Optional.empty();
        }

        if (!SelectMenuEvent.class.isAssignableFrom(method.getParameters()[0].getType())) {
            log.error("An error has occurred! Skipping Button {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException(String.format("First parameter must be of type %s!", SelectMenuEvent.class.getSimpleName())));
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
                selectMenu.ephemeral(),
                Set.of(selectMenu.value()),
                defaultValueSet,
                new HashSet<>(Set.of(selectMenu.channelTypes())),
                selectMenu.placeholder(),
                selectMenu.minValue(),
                selectMenu.maxValue()
        ));
    }

    public net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu toEntitySelectMenu(String id, boolean enabled) {
        var menu = net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.create(id, selectTargets)
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

    /**
     * Gets the placeholder string.
     *
     * @return the placeholder string
     */
    public String getPlaceholder() {
        return placeholder;
    }

    /**
     * Gets the minimum value.
     *
     * @return the minimum value
     */
    public int getMinValue() {
        return minValue;
    }

    /**
     * Gets the maximum value.
     *
     * @return the maximum value
     */
    public int getMaxValue() {
        return maxValue;
    }

    /**
     * Gets the runtime id. The runtime id is composed of the static interaction id and the
     * snowflake id of the interaction event that created the runtime.
     *
     * @param context the {@link CommandContext} this button will be attached to
     * @return the runtime id
     */
    @NotNull
    public String getRuntimeId(GenericContext<? extends GenericInteractionCreateEvent> context) {
        return String.format("%s.%s", getId(), context.getRuntime().getInstanceId());
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
                ", ephemeral=" + ephemeral +
                ", id='" + id + '\'' +
                ", method=" + method +
                '}';
    }
}
