package io.github.kaktushose.jdac.dispatching.reply.internal;

import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition.ReplyConfig;
import io.github.kaktushose.jdac.exceptions.internal.JDACException;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static io.github.kaktushose.jdac.dispatching.context.internal.RichInvocationContext.*;

@ApiStatus.Internal
public final class MessageReplyAction extends ReplyAction {

    private static final Logger log = LoggerFactory.getLogger(MessageReplyAction.class);

    public MessageReplyAction(ReplyConfig replyConfig) {
        log.debug("Reply Debug: [Runtime={}]", getRuntime().id());
        super(replyConfig, new MessageCreateBuilder());
    }

    public Message reply(String message, Entry... placeholder) {
        builder.setContent(getFramework().messageResolver().resolve(message, getUserLocale(), placeholder));
        return reply();
    }

    public Message reply(MessageEmbed first, MessageEmbed... additional) {
        builder.setEmbeds(Stream.concat(Stream.of(first), Arrays.stream(additional)).toList());
        return reply();
    }

    public Message reply(MessageCreateData data) {
        builder = MessageCreateBuilder.from(data);
        return reply();
    }

    public void builder(Consumer<MessageCreateBuilder> builder) {
        builder.accept(this.builder);
        // this API only works for CV1 and underlying parts rely on no CV2 being present
        if (this.builder.isUsingComponentsV2()) {
            throw new IllegalArgumentException(JDACException.errorMessage("illegal-cv2-usage"));
        }
    }

    public void addComponents(MessageTopLevelComponent... components) {
        builder.addComponents(components);
    }

    public void addEmbeds(MessageEmbed... embeds) {
        builder.addEmbeds(embeds);
    }
}
