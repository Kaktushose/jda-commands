package com.github.kaktushose.jda.commands.dispatching.interactions.autocomplete;

import com.github.kaktushose.jda.commands.dispatching.interactions.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import javax.annotation.Nonnull;
import java.util.Collection;

public class AutoCompleteEvent extends GenericEvent {

    private final CommandAutoCompleteInteractionEvent event;

    protected AutoCompleteEvent(AutoCompleteContext context) {
        super(GenericEvent.fromEvent(context.getEvent()));
        event = context.getEvent();
    }

    public void replyChoices(Collection<Command.Choice> choices) {
        event.replyChoices(choices).queue();
    }

    /**
     * The name of the input field, usually an option name in {@link CommandAutoCompleteInteraction}.
     *
     * @return The option name
     */
    @Nonnull
    public String getName() {
        return event.getFocusedOption().getName();
    }

    /**
     * The query value that the user is currently typing.
     *
     * <p>This is not validated and may not be a valid value for an actual command.
     * For instance, a user may input invalid numbers for {@link OptionType#NUMBER}.
     *
     * @return The current auto-completable query value
     */
    @Nonnull
    public String getValue() {
        return event.getFocusedOption().getValue();
    }

    /**
     * The expected option type for this query.
     *
     * @return The option type expected from this auto-complete response
     */
    @Nonnull
    public OptionType getType() {
        return event.getFocusedOption().getType();
    }
}
