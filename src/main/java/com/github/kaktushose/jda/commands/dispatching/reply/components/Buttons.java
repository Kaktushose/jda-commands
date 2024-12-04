package com.github.kaktushose.jda.commands.dispatching.reply.components;

import com.github.kaktushose.jda.commands.dispatching.reply.Replyable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * {@link Component} implementation for buttonContainers. This class can be used to add
 * {@link com.github.kaktushose.jda.commands.annotations.interactions.Button Buttons} to messages while defining their
 * state (enabled or disabled).
 *
 * @see Replyable#with(Component...)
 * @since 2.3.0
 */
public record Buttons(Collection<ButtonContainer> buttonContainers) implements Component {

    public Buttons(Collection<ButtonContainer> buttonContainers) {
        this.buttonContainers = Collections.unmodifiableCollection(buttonContainers);
    }

    /**
     * Add the buttonContainers with the given ids to the reply message as enabled.
     *
     * @param buttons the id of the buttonContainers to add
     * @return instance of this class used inside the
     * {@link com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext}
     */
    public static Buttons enabled(String... buttons) {
        return build(true, buttons);
    }

    /**
     * Add the buttonContainers with the given ids to the reply message as disabled.
     *
     * @param buttons the id of the buttonContainers to add
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
     * Contains information about a single {@link com.github.kaktushose.jda.commands.annotations.interactions.Button Button}.
     */
    public record ButtonContainer(String name, boolean enabled) {
    }
}
