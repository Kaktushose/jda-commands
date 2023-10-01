package com.github.kaktushose.jda.commands.dispatching.buttons;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.components.Buttons;
import com.github.kaktushose.jda.commands.components.Component;
import com.github.kaktushose.jda.commands.dispatching.GenericEvent;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.dispatching.reply.Replyable;
import com.github.kaktushose.jda.commands.reflect.interactions.ButtonDefinition;
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
 * access to the {@link ButtonDefinition} object which describes the button interaction that is executed.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see GenericEvent
 * @see Replyable
 * @since 1.0.0
 */
public class ButtonEvent extends GenericEvent implements Replyable {

    private final ButtonDefinition button;
    private final ButtonContext context;
    private final ReplyContext replyContext;

    /**
     * Constructs a CommandEvent.
     *
     * @param button the underlying {@link ButtonDefinition} object
     * @param context the {@link ButtonContext}
     */
    public ButtonEvent(@NotNull ButtonDefinition button, @NotNull ButtonContext context) {
        super(GenericEvent.fromEvent(context.getEvent()));
        this.button = button;
        this.context = context;
        replyContext = new ReplyContext(context);
    }

    /**
     * Get the {@link ButtonDefinition} object which describes the button that is executed.
     *
     * @return the underlying {@link ButtonDefinition} object
     */
    public ButtonDefinition getButtonDefinition() {
        return button;
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
     * Get the {@link ButtonContext} object.
     *
     * @return the registered {@link ButtonContext} object
     */
    public ButtonContext getButtonContext() {
        return context;
    }


    @Override
    public Replyable with(@NotNull Component... components) {
        List<ItemComponent> items = new ArrayList<>();
        ButtonDefinition definition = button;
        for (Component component : components) {
            if (component instanceof Buttons) {
                Buttons buttons = (Buttons) component;
                buttons.getButtons().forEach(button -> {
                    String id = String.format("%s.%s", definition.getMethod().getDeclaringClass().getSimpleName(), button.getId());
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
        }

        if (items.size() > 0) {
            getReplyContext().getBuilder().addComponents(ActionRow.of(items));
        }
        return this;
    }

    @Override
    public @NotNull ReplyContext getReplyContext() {
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
