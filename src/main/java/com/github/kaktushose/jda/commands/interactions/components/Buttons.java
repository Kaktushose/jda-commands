package com.github.kaktushose.jda.commands.interactions.components;

import java.util.*;

/**
 * {@link Component} implementation for buttons. This class can be used to add
 * {@link com.github.kaktushose.jda.commands.annotations.interactions.Button Buttons} to messages while defining their
 * state (enabled or disabled).
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see com.github.kaktushose.jda.commands.dispatching.sender.ReplyAction#with(Component...) ReplyAction#with(Component...)
 * @since 2.3.0
 */
public class Buttons implements Component {

    private final Collection<ButtonContainer> buttons;

    public Buttons(Collection<ButtonContainer> buttons) {
        this.buttons = buttons;
    }

    public static Buttons enabled(String... buttons) {
        return build(true, buttons);
    }

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

    public Collection<ButtonContainer> getButtons() {
        return buttons;
    }

    /**
     * Contains information about a single {@link com.github.kaktushose.jda.commands.annotations.interactions.Button}.
     */
    public static class ButtonContainer {
        private final String id;
        private final boolean enabled;

        private ButtonContainer(String name, boolean enabled) {
            this.id = name;
            this.enabled = enabled;
        }

        /**
         * Gets the button id.
         *
         * @return the button id
         */
        public String getId() {
            return id;
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
