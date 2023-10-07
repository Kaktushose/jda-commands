package com.github.kaktushose.jda.commands.dispatching.menus;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.GenericEvent;
import com.github.kaktushose.jda.commands.dispatching.buttons.ButtonContext;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.dispatching.reply.Replyable;
import com.github.kaktushose.jda.commands.reflect.interactions.ButtonDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.menus.EntitySelectMenuDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.menus.GenericSelectMenuDefinition;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * This class is a subclass of {@link GenericEvent}.
 * It provides additional features for sending messages or editing the original message and also grants
 * access to the {@link EntitySelectMenuDefinition} object which describes the select menu interaction that is executed.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @see GenericEvent
 * @see Replyable
 * @since 4.0.0
 */
public class SelectMenuEvent extends GenericEvent implements Replyable {

    private final GenericSelectMenuDefinition<? extends SelectMenu> selectMenu;
    private final SelectMenuContext context;
    private final ReplyContext replyContext;

    /**
     * Constructs a ButtonEvent.
     *
     * @param selectMenu the underlying {@link ButtonDefinition} object
     * @param context the {@link ButtonContext}
     */
    public SelectMenuEvent(@NotNull GenericSelectMenuDefinition<? extends SelectMenu> selectMenu, @NotNull SelectMenuContext context) {
        super(GenericEvent.fromEvent(context.getEvent()));
        this.selectMenu = selectMenu;
        this.context = context;
        replyContext = new ReplyContext(context);
    }

    /**
     * Get the {@link EntitySelectMenuDefinition} object which describes the button that is executed.
     *
     * @return the underlying {@link EntitySelectMenuDefinition} object
     */
    public GenericSelectMenuDefinition<? extends SelectMenu> getSelectMenu() {
        return selectMenu;
    }

    /**
     * Get the {@link SelectMenuContext} object.
     *
     * @return the registered {@link SelectMenuContext} object
     */
    @Override
    public SelectMenuContext getContext() {
        return context;
    }

    /**
     * Get the {@link JDACommands} object.
     *
     * @return the {@link JDACommands} object
     */
    public JDACommands getJdaCommands() {
        return context.getJdaCommands();
    }

    public ReplyContext getReplyContext() {
        return replyContext;
    }

    @Override
    public void reply() {
        Optional<MessageCreateData> optional = context.getRuntime().getLatestReply();
        if (optional.isPresent()) {
            MessageCreateData cached = optional.get();
            if (replyContext.isKeepComponents() && replyContext.getBuilder().getComponents().isEmpty()) {
                replyContext.getBuilder().setComponents(cached.getComponents());
            }

        }
        replyContext.queue();
        context.getRuntime().setLatestReply(replyContext.toMessageCreateData());
    }

}
