package com.github.kaktushose.jda.commands.dispatching.reply.components;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * {@link Component} implementation for select menus. This class can be used to add
 * {@link com.github.kaktushose.jda.commands.annotations.interactions.StringSelectMenu StringSelectMenus} or
 * {@link com.github.kaktushose.jda.commands.annotations.interactions.EntitySelectMenu EntitySelectMenus}
 * to messages while defining their state (enabled or disabled).
 *
 * @see Replyable#with(Component...)
 * @since 2.3.0
 */
public record SelectMenus(Collection<SelectMenus.SelectMenuContainer> selectMenuContainers) implements Component {

    public SelectMenus(Collection<SelectMenus.SelectMenuContainer> selectMenuContainers) {
        this.selectMenuContainers = Collections.unmodifiableCollection(selectMenuContainers);
    }

    /**
     * Add the select menus with the given ids to the reply message as enabled.
     *
     * @param menus the id of the select menus to add
     * @return instance of this class used inside the
     * {@link ReplyBuilder}
     */
    public static SelectMenus enabled(String... menus) {
        return build(true, menus);
    }

    /**
     * Add the  select menus with the given ids to the reply message as disabled.
     *
     * @param menus the id of the select menus to add
     * @return instance of this class used inside the
     * {@link ReplyBuilder}
     */
    public static SelectMenus disabled(String... menus) {
        return build(false, menus);
    }

    private static SelectMenus build(boolean enabled, String... menus) {
        List<SelectMenus.SelectMenuContainer> result = new ArrayList<>();
        for (String menu : menus) {
            result.add(new SelectMenus.SelectMenuContainer(menu, enabled));
        }
        return new SelectMenus(result);
    }

    /**
     * Contains information about a single select menu (either StringSelectMenu or EntitySelectMenu).
     */
    @ApiStatus.Internal
    public record SelectMenuContainer(String name, boolean enabled) {
    }
}
