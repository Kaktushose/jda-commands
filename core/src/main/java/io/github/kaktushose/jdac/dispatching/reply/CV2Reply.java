package io.github.kaktushose.jdac.dispatching.reply;

import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition.ReplyConfig;
import io.github.kaktushose.jdac.exceptions.InternalException;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IDeferrableCallback;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;


import static io.github.kaktushose.jdac.dispatching.context.internal.RichInvocationContext.getJdaEvent;
import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

public final class CV2Reply {

    private final MessageCreateBuilder builder;
    private final ReplyConfig replyConfig;

    public CV2Reply(MessageTopLevelComponent component, MessageTopLevelComponent[] components, ReplyConfig replyConfig) {
        this.replyConfig = replyConfig;
        builder = new MessageCreateBuilder();
        builder.useComponentsV2().addComponents(component).addComponents(components);
    }

    public CV2Reply cv2(MessageTopLevelComponent... component) {
        builder.addComponents(component);
        return this;
    }

    public Message reply() {
        GenericInteractionCreateEvent jdaEvent = getJdaEvent();

        boolean editReply = replyConfig.editReply();
        switch (jdaEvent) {
            case ModalInteractionEvent modalEvent when modalEvent.getMessage() != null && replyConfig.editReply() ->
                    deferEdit(modalEvent);
            case IMessageEditCallback callback when replyConfig.editReply() -> deferEdit(callback);
            case IReplyCallback callback -> deferReply(callback);
            default -> throw new InternalException("reply-failed", entry("getJdaEvent()", jdaEvent.getClass().getName()));
        }
        if (jdaEvent instanceof ModalInteractionEvent modalEvent) {
            editReply = modalEvent.getMessage() != null;
        }
        var hook = ((IDeferrableCallback) jdaEvent).getHook();

//        if (jdaEvent instanceof ComponentInteraction interaction && keepComponents) {
//            builder.addComponents(retrieveComponents(interaction.getMessage()));
//        }

        if (editReply) {
            return hook.editOriginal(MessageEditData.fromCreateData(builder.build())).complete();
        }
        return hook.setEphemeral(replyConfig.ephemeral()).sendMessage(builder.build()).complete();
    }


    private void deferReply(IReplyCallback callback) {
        if (!getJdaEvent().isAcknowledged()) {
            callback.deferReply(replyConfig.ephemeral()).queue();
        }
    }

    private void deferEdit(IMessageEditCallback callback) {
        if (!getJdaEvent().isAcknowledged()) {
            callback.deferEdit().queue();
        }
    }

}
