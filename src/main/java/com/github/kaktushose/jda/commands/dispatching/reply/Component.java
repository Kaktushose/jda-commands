package com.github.kaktushose.jda.commands.dispatching.reply;

import java.util.Arrays;

/**
 * Marker interface for components that can be added to messages.
 *
 * @see Replyable#with(Component...) ReplyAction#with(Component...)
 * @see Buttons
 * @see SelectMenus
 * @since 2.3.0
 */
public sealed interface Component {

    String name();
    boolean enabled();
    boolean staticComponent();

    record Button(String name, boolean enabled, boolean staticComponent) implements Component {}

    static Button[] buttons(String... buttons) {
        return buttons(true, false, buttons);
    }

    static Button[] buttons(boolean enabled, String... buttons) {
        return buttons(enabled, false, buttons);
    }

    static Button[] buttons(boolean enabled, boolean staticComponent, String... buttons) {
        return Arrays.stream(buttons)
                .map(s -> new Button(s, enabled, staticComponent))
                .toArray(Button[]::new);
    }

    record SelectMenu(String name, boolean enabled, boolean staticComponent) implements Component {}

    static SelectMenu[] selectMenus(String... menus) {
        return selectMenus(true, false, menus);
    }

    static SelectMenu[] selectMenus(boolean enabled, String... menus) {
        return selectMenus(enabled, false, menus);
    }

    static SelectMenu[] selectMenus(boolean enabled, boolean staticComponent, String... menus) {
        return Arrays.stream(menus)
                .map(s -> new SelectMenu(s, enabled, staticComponent))
                .toArray(SelectMenu[]::new);
    }

}
