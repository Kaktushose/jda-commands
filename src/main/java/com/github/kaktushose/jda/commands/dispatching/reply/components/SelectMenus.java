package com.github.kaktushose.jda.commands.dispatching.reply.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SelectMenus implements Component {

    private final Collection<SelectMenus.SelectMenuContainer> selectMenus;

    private SelectMenus(Collection<SelectMenus.SelectMenuContainer> selectMenus) {
        this.selectMenus = selectMenus;
    }

    public static SelectMenus enabled(String... buttons) {
        return build(true, buttons);
    }

    public static SelectMenus disabled(String... buttons) {
        return build(false, buttons);
    }

    private static SelectMenus build(boolean enabled, String... menus) {
        List<SelectMenus.SelectMenuContainer> result = new ArrayList<>();
        for (String menu : menus) {
            result.add(new SelectMenus.SelectMenuContainer(menu, enabled));
        }
        return new SelectMenus(result);
    }

    public Collection<SelectMenus.SelectMenuContainer> getSelectMenus() {
        return selectMenus;
    }

    /**
     * Contains information about a single {@link com.github.kaktushose.jda.commands.annotations.interactions.Button Button}.
     */
    public static class SelectMenuContainer {
        private final String id;
        private final boolean enabled;

        private SelectMenuContainer(String name, boolean enabled) {
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
