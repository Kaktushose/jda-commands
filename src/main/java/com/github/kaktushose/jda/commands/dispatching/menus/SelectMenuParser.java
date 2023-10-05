package com.github.kaktushose.jda.commands.dispatching.menus;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.GenericParser;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * An implementation of {@link GenericParser} that can parse {@link ButtonInteractionEvent}.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @since 4.0.0
 */
public class SelectMenuParser extends GenericParser<GenericSelectMenuInteractionEvent<?, ?>> {

    /**
     * Takes a {@link GenericSelectMenuInteractionEvent}, parses and transpiles it into a {@link SelectMenuContext}.
     *
     * @param event       the {@link GenericSelectMenuInteractionEvent} to parse
     * @param jdaCommands the {@link JDACommands} instance
     * @return a new {@link SelectMenuContext}
     */
    @Override
    public @NotNull GenericContext<? extends GenericInteractionCreateEvent> parse(@NotNull GenericSelectMenuInteractionEvent<?, ?> event, @NotNull JDACommands jdaCommands) {
        return new SelectMenuContext(event, jdaCommands);
    }
}
