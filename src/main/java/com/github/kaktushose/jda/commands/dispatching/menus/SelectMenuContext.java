package com.github.kaktushose.jda.commands.dispatching.menus;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.reflect.interactions.menus.EntitySelectMenuDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.menus.GenericSelectMenuDefinition;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

/**
 * Implementation of {@link GenericContext} for {@link GenericSelectMenuInteractionEvent}.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @since 4.0.0
 */
public class SelectMenuContext extends GenericContext<GenericSelectMenuInteractionEvent<?, ?>> {

    private GenericSelectMenuDefinition<? extends SelectMenu> selectMenu;

    /**
     * Constructs a new SelectMenuContext.
     *
     * @param event       the corresponding {@link GenericSelectMenuInteractionEvent}
     * @param jdaCommands the corresponding {@link JDACommands} instance
     */
    public SelectMenuContext(GenericSelectMenuInteractionEvent<?, ?> event, JDACommands jdaCommands) {
        super(event, jdaCommands);
    }

    /**
     * Gets the {@link EntitySelectMenuDefinition}.
     *
     * @return the {@link EntitySelectMenuDefinition}
     */
    public GenericSelectMenuDefinition<? extends SelectMenu> getSelectMenu() {
        return selectMenu;
    }

    /**
     * Set the {@link EntitySelectMenuDefinition}.
     *
     * @param selectMenu the {@link EntitySelectMenuDefinition}
     * @return the current CommandContext instance
     */
    public SelectMenuContext setSelectMenu(GenericSelectMenuDefinition<? extends SelectMenu> selectMenu) {
        this.selectMenu = selectMenu;
        return this;
    }
}
