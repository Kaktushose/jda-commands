package com.github.kaktushose.jda.commands.dispatching.buttons;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.GenericEvent;
import com.github.kaktushose.jda.commands.dispatching.sender.EditAction;
import com.github.kaktushose.jda.commands.dispatching.sender.EditCallback;
import com.github.kaktushose.jda.commands.dispatching.sender.ReplyAction;
import com.github.kaktushose.jda.commands.dispatching.sender.ReplyCallback;
import com.github.kaktushose.jda.commands.dispatching.sender.impl.InteractionEditCallback;
import com.github.kaktushose.jda.commands.dispatching.sender.impl.InteractionReplyCallback;
import com.github.kaktushose.jda.commands.embeds.help.HelpMessageFactory;
import com.github.kaktushose.jda.commands.interactions.components.Buttons;
import com.github.kaktushose.jda.commands.interactions.components.Component;
import com.github.kaktushose.jda.commands.reflect.interactions.ButtonDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.CommandDefinition;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class is a subclass of {@link GenericEvent}.
 * It provides additional features for sending messages or editing the original message and also grants
 * access to the {@link ButtonDefinition} object which describes the button interaction that is executed.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see GenericEvent
 * @see ReplyAction
 * @see EditAction
 * @since 1.0.0
 */
public class ButtonEvent extends GenericEvent implements ReplyAction, EditAction {

    private final ButtonDefinition button;
    private final JDACommands jdaCommands;
    private final Collection<ActionRow> actionRows;
    private final ButtonInteractionEvent event;
    private ReplyCallback replyCallback;
    private EditCallback editCallback;

    public ButtonEvent(ButtonInteractionEvent event, ButtonDefinition button, JDACommands jdaCommands) {
        super(event.getJDA(), event.getResponseNumber(), event.getGuild(), event.getUser(), event.getMember(), event.getChannel(), event.getChannelType(), event.getMessage());
        this.event = event;
        this.button = button;
        this.jdaCommands = jdaCommands;
        actionRows = new ArrayList<>();
        replyCallback = new InteractionReplyCallback(event, actionRows);
        editCallback = new InteractionEditCallback(event, actionRows);
    }

    @Override
    public ButtonEvent editComponents(@NotNull Component... components) {
        List<ItemComponent> items = new ArrayList<>();
        for (Component component : components) {
            if (!(component instanceof Buttons)) {
                return this;
            }
            Buttons buttons = (Buttons) component;
//            buttons.getButtons().forEach(container -> {
//                String id = String.format("%s.%s", button.getMethod().getDeclaringClass().getSimpleName(), container.getId());
//                button.getController().getButtons()
//                        .stream()
//                        .filter(it -> it.getId().equals(id))
//                        .findFirst()
//                        .map(it -> it.toButton().withDisabled(!container.isEnabled()))
//                        .ifPresent(items::add);
//            });
//        }
//        if (items.size() > 0) {
//            editCallback.editComponents(ActionRow.of(items));
//        } else {
//            editCallback.editComponents();
        }
        return this;
    }

    @SuppressWarnings("ConstantConditions")
    public ButtonEvent with(@NotNull Component... components) {
        List<ItemComponent> items = new ArrayList<>();
        for (Component component : components) {
            if (!(component instanceof Buttons)) {
                return this;
            }
            Buttons buttons = (Buttons) component;
//            buttons.getButtons().forEach(container -> {
//                String id = String.format("%s.%s", button.getMethod().getDeclaringClass().getSimpleName(), container.getId());
//                button.getController().getButtons()
//                        .stream()
//                        .filter(it -> it.getId().equals(id))
//                        .findFirst()
//                        .map(it -> it.toButton().withDisabled(!container.isEnabled()))
//                        .ifPresent(items::add);
//            });
        }
        if (items.size() > 0) {
            actionRows.add(ActionRow.of(items));
        }
        return this;
    }

    @NotNull
    @Override
    public ReplyCallback getReplyCallback() {
        return replyCallback;
    }

    /**
     * Sets the {@link ReplyCallback} used to send replies to this event.
     *
     * @param replyCallback the {@link ReplyCallback} to use
     */
    public void setReplyCallback(ReplyCallback replyCallback) {
        this.replyCallback = replyCallback;
    }

    @NotNull
    @Override
    public EditCallback getEditCallback() {
        return editCallback;
    }

    /**
     * Sets the {@link EditCallback} used to send edits to this event.
     *
     * @param editCallback the {@link EditCallback} to use
     */
    public void setEditCallback(EditCallback editCallback) {
        this.editCallback = editCallback;
    }

    @Override
    public boolean isEphemeral() {
        return button.isEphemeral();
    }

    /**
     * Get the {@link CommandDefinition} object which describes the command that is executed.
     *
     * @return the underlying {@link CommandDefinition} object
     */
    public ButtonDefinition getCommandDefinition() {
        return button;
    }

    /**
     * Get the {@link JDACommands} object.
     *
     * @return the {@link JDACommands} object
     */
    public JDACommands getJdaCommands() {
        return jdaCommands;
    }

    /**
     * Get the registered {@link HelpMessageFactory} object.
     *
     * @return the registered {@link HelpMessageFactory} object
     */
    public HelpMessageFactory getHelpMessageFactory() {
        return getJdaCommands().getImplementationRegistry().getHelpMessageFactory();
    }

    /**
     * Gets the {@link InteractionHook}.
     *
     * @return the {@link InteractionHook}.
     */
    public InteractionHook getInteractionHook() {
        return event.getHook();
    }

    /**
     * Gets the {@link ActionRow ActionRows} already added to the reply.
     *
     * @return a possibly-empty {@link List} of {@link ActionRow ActionRows}.
     */
    public List<ActionRow> getActionRows() {
        return new ArrayList<>(actionRows);
    }

}
