package com.github.kaktushose.jda.commands.dispatching.menus;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.reflect.interactions.EntitySelectMenuDefinition;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;

/**
 * Implementation of {@link GenericContext} for {@link GenericSelectMenuInteractionEvent}.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @since 4.0.0
 */
public class SelectMenuContext extends GenericContext<GenericSelectMenuInteractionEvent<?, ?>> {

    private EntitySelectMenuDefinition selectMenu;

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
    public EntitySelectMenuDefinition getSelectMenu() {
        return selectMenu;
    }

    /**
     * Set the {@link EntitySelectMenuDefinition}.
     *
     * @param selectMenu the {@link EntitySelectMenuDefinition}
     * @return the current CommandContext instance
     */
    public SelectMenuContext setSelectMenu(EntitySelectMenuDefinition selectMenu) {
        this.selectMenu = selectMenu;
        return this;
    }
}
