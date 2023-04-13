package com.github.kaktushose.jda.commands.dispatching.commands;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.GenericEvent;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.dispatching.reply.Replyable;
import com.github.kaktushose.jda.commands.components.Buttons;
import com.github.kaktushose.jda.commands.components.Component;
import com.github.kaktushose.jda.commands.reflect.interactions.CommandDefinition;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a subclass of {@link GenericEvent}.
 * It provides some additional features for sending messages and also grants
 * access to the {@link CommandDefinition} object which describes the command that is executed.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @see GenericEvent
 * @see Replyable
 * @since 1.0.0
 */
public class CommandEvent extends GenericEvent implements Replyable {

    private final CommandDefinition command;
    private final CommandContext context;
    private final ReplyContext replyContext;

    /**
     * Constructs a CommandEvent.
     *
     * @param command the underlying {@link CommandDefinition} object
     * @param context the {@link GenericContext}
     */
    public CommandEvent(@NotNull CommandDefinition command, @NotNull CommandContext context) {
        super(GenericEvent.fromEvent(context.getEvent()));
        this.command = command;
        this.context = context;
        replyContext = new ReplyContext(context);
    }

    /**
     * Get the {@link CommandDefinition} object which describes the command that is executed.
     *
     * @return the underlying {@link CommandDefinition} object
     */
    public CommandDefinition getCommandDefinition() {
        return command;
    }

    /**
     * Get the {@link JDACommands} object.
     *
     * @return the {@link JDACommands} object
     */
    public JDACommands getJdaCommands() {
        return context.getJdaCommands();
    }

    /**
     * Get the {@link GenericContext} object.
     *
     * @return the registered {@link GenericContext} object
     */
    public CommandContext getCommandContext() {
        return context;
    }

    @Override
    public Replyable with(@NotNull Component... components) {
        List<ItemComponent> items = new ArrayList<>();
        for (Component component : components) {
            if (component instanceof Buttons) {
                Buttons buttons = (Buttons) component;
                buttons.getButtons().forEach(button -> {
                    String id = String.format("%s.%s", command.getMethod().getDeclaringClass().getSimpleName(), button.getId());
                    getJdaCommands().getInteractionRegistry().getButtons()
                            .stream()
                            .filter(it -> it.getId().equals(id))
                            .findFirst()
                            .map(it -> it.toButton().withDisabled(!button.isEnabled()).withId(it.getRuntimeId(context)))
                            .ifPresent(items::add);
                });
            }
        }

        if (items.size() > 0) {
            getReplyContext().getBuilder().addComponents(ActionRow.of(items));
        }
        return this;
    }

    @Override
    public ReplyContext getReplyContext() {
        return replyContext;
    }

    @Override
    public void reply() {
        replyContext.queue();
    }
}
