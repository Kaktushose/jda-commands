package com.github.kaktushose.jda.commands.dispatching.menus;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.components.Buttons;
import com.github.kaktushose.jda.commands.components.Component;
import com.github.kaktushose.jda.commands.components.SelectMenus;
import com.github.kaktushose.jda.commands.dispatching.GenericEvent;
import com.github.kaktushose.jda.commands.dispatching.buttons.ButtonContext;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.dispatching.reply.Replyable;
import com.github.kaktushose.jda.commands.reflect.interactions.ButtonDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.menus.EntitySelectMenuDefinition;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
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

    private final EntitySelectMenuDefinition selectMenu;
    private final SelectMenuContext context;
    private final ReplyContext replyContext;

    /**
     * Constructs a ButtonEvent.
     *
     * @param selectMenu the underlying {@link ButtonDefinition} object
     * @param context the {@link ButtonContext}
     */
    public SelectMenuEvent(@NotNull EntitySelectMenuDefinition selectMenu, @NotNull SelectMenuContext context) {
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
    public EntitySelectMenuDefinition getSelectMenu() {
        return selectMenu;
    }

    /**
     * Get the {@link SelectMenuContext} object.
     *
     * @return the registered {@link SelectMenuContext} object
     */
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

    @Override
    public Replyable with(@NotNull Component... components) {
        List<ItemComponent> items = new ArrayList<>();
        for (Component component : components) {
            if (component instanceof Buttons) {
                Buttons buttons = (Buttons) component;
                buttons.getButtons().forEach(button -> {
                    String id = String.format("%s.%s", selectMenu.getMethod().getDeclaringClass().getSimpleName(), button.getId());
                    getJdaCommands().getInteractionRegistry().getButtons()
                            .stream()
                            .filter(it -> it.getId().equals(id))
                            .findFirst()
                            .map(it -> {
                                Button jdaButton = it.toButton().withDisabled(!button.isEnabled());
                                //only assign ids to non-link buttons
                                if (jdaButton.getUrl() == null) {
                                    jdaButton = jdaButton.withId(it.getRuntimeId(context));
                                }
                                return jdaButton;
                            }).ifPresent(items::add);
                });
            }
            if (component instanceof SelectMenus) {
                SelectMenus menus = (SelectMenus) component;
                menus.getSelectMenus().forEach(menu -> {
                    String id = String.format("%s.%s", selectMenu.getMethod().getDeclaringClass().getSimpleName(), menu.getId());
                    getJdaCommands().getInteractionRegistry().getEntitySelectMenus()
                            .stream()
                            .filter(it -> it.getId().equals(id))
                            .findFirst().map(it -> it.toEntitySelectMenu(it.getRuntimeId(context), menu.isEnabled()))
                            .ifPresent(items::add);
                });
            }
        }

        if (items.size() > 0) {
            getReplyContext().getBuilder().addComponents(ActionRow.of(items));
        }
        return this;
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
