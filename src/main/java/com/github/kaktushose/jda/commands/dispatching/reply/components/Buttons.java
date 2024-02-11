package com.github.kaktushose.jda.commands.dispatching.reply.components;

import com.github.kaktushose.jda.commands.dispatching.reply.Replyable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@link Component} implementation for buttons. This class can be used to add
 * {@link com.github.kaktushose.jda.commands.annotations.interactions.Button Buttons} to messages while defining their
 * state (enabled or disabled).
 *
 * @see Replyable#with(Component...)
 * @since 2.3.0
 */
public class Buttons implements Component {

    private final Collection<ButtonContainer> buttons;

    private Buttons(Collection<ButtonContainer> buttons) {
        this.buttons = buttons;
    }

    /**
     * Add the buttons with the given ids to the reply message as enabled.
     *
     * @param buttons the id of the buttons to add
     * @return instance of this class used inside the
     * {@link com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext}
     */
    public static Buttons enabled(String... buttons) {
        return build(true, buttons);
    }

    /**
     * Add the buttons with the given ids to the reply message as disabled.
     *
     * @param buttons the id of the buttons to add
     * @return instance of this class used inside the
     * {@link com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext}
     */
    public static Buttons disabled(String... buttons) {
        return build(false, buttons);
    }

    private static Buttons build(boolean enabled, String... buttons) {
        List<ButtonContainer> result = new ArrayList<>();
        for (String button : buttons) {
            result.add(new ButtonContainer(button, enabled));
        }
        return new Buttons(result);
    }

    /**
     * Gets the {@link ButtonContainer}.
     *
     * @return the {@link ButtonContainer}
     */
    public Collection<ButtonContainer> getButtonContainer() {
        return buttons;
    }

    /**
     * Contains information about a single {@link com.github.kaktushose.jda.commands.annotations.interactions.Button Button}.
     */
    public static class ButtonContainer {
        private final String name;
        private final boolean enabled;

        private ButtonContainer(String name, boolean enabled) {
            this.name = name;
            this.enabled = enabled;
        }

        /**
         * Gets the button id.
         *
         * @return the button id
         */
        public String getName() {
            return name;
        }

        /**
         * Whether the button is enabled or not.
         *
         * @return {@code true} if the button is enabled
         */
        public boolean isEnabled() {
            return enabled;
        }
    }
}
