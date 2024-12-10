package com.github.kaktushose.jda.commands.annotations.interactions;

import com.github.kaktushose.jda.commands.dispatching.events.ComponentEvent;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.SelectTarget;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated with EntitySelectMenu will be registered as a EntitySelectMenu at startup.
 *
 * <p>Therefore the method must be declared inside a class that is annotated with
 * {@link Interaction}.
 * Furthermore, the method signature has to meet the following conditions:
 * <ul>
 * <li>First parameter must be of type
 * {@link ComponentEvent SelectMenuEvent}</li>
 * </ul>
 *
 * @see Interaction
 * @since 2.3.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntitySelectMenu {

    /**
     * Supported {@link SelectTarget SelectTargets}.
     *
     * @return an array of supported {@link SelectTarget SelectTargets}
     */
    SelectTarget[] value();

    /**
     * The {@link net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.DefaultValue default values}
     * for roles that will be shown to the user.
     *
     * @return the {@link net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.DefaultValue default values}
     */
    long[] defaultRoles() default -1;

    /**
     * The {@link net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.DefaultValue default values}
     * for channels that will be shown to the user.
     *
     * @return the {@link net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.DefaultValue default values}
     */
    long[] defaultChannels() default -1;

    /**
     * The {@link net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.DefaultValue default values}
     * for users that will be shown to the user.
     *
     * @return the {@link net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.DefaultValue default values}
     */
    long[] defaultUsers() default -1;

    /**
     * The {@link ChannelType ChannelTypes} that should be supported by this menu.
     * This is only relevant for menus that allow CHANNEL targets.
     *
     * @return the {@link ChannelType ChannelTypes} that should be supported by this menu
     */
    ChannelType[] channelTypes() default ChannelType.UNKNOWN;

    /**
     * Configure the placeholder which is displayed when no selections have been made yet.
     *
     * @return the placeholder which is displayed when no selections have been made yet
     */
    String placeholder() default "";

    /**
     * The minimum amount of values a user has to select.
     * <br>Default: {@code 1}
     *
     * <p>The minimum must not exceed the amount of available options.
     *
     * @return the minimum amount of values a user has to select
     */
    int minValue() default 1;

    /**
     * The maximum amount of values a user can select.
     * <br>Default: {@code 1}
     *
     * <p>The maximum must not exceed the amount of available options.
     *
     * @return the maximum amount of values a user can select
     */
    int maxValue() default 1;

    /**
     * Whether this button should send ephemeral replies by default.
     *
     * @return {@code true} if to send ephemeral replies
     */
    boolean ephemeral() default false;

}
