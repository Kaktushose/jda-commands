package io.github.kaktushose.jdac.testing.invocation;

import io.github.kaktushose.jdac.testing.reply.MessageEventReply;
import io.github.kaktushose.jdac.testing.TestScenario.Context;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionType;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public final class SlashCommandInvocation extends ModalReplyableInvocation<SlashCommandInteractionEvent> {

    private final Map<String, OptionMapping> optionMappings = new HashMap<>();

    public SlashCommandInvocation(Context context, String command) {
        super(context, SlashCommandInteractionEvent.class, InteractionType.COMMAND);

        when(event.getFullCommandName()).thenReturn(command);
        lenient().when(event.getOption(anyString())).then(invocation -> optionMappings.get((String) invocation.getArgument(0)));
        lenient().when(event.deferReply(anyBoolean())).thenReturn(mock(ReplyCallbackAction.class));

        lenient().when(event.getInteraction()).thenReturn(mock(SlashCommandInteraction.class));

        lenient().when(event.replyModal(any(Modal.class))).then(invocation -> {
            modal.complete(invocation.getArgument(0));
            return mock(ModalCallbackAction.class);
        });
    }

    @Override
    public MessageEventReply invoke() {
        return super.invoke();
    }

    public SlashCommandInvocation option(String name, OptionMapping mapping) {
        optionMappings.put(name, mapping);
        return this;
    }

    public SlashCommandInvocation channel(MessageChannelUnion channel) {
        when(event.getChannel()).thenReturn(channel);
        return this;
    }

    public static class Option {

        public static OptionMapping string(String value) {
            return mapping(OptionType.STRING, OptionMapping::getAsString, value);
        }

        public static OptionMapping integer(Integer value) {
            return mapping(OptionType.INTEGER, OptionMapping::getAsInt, value);
        }

        public static OptionMapping bool(Boolean value) {
            return mapping(OptionType.BOOLEAN, OptionMapping::getAsBoolean, value);
        }

        public static OptionMapping user(User value) {
            return mapping(OptionType.USER, OptionMapping::getAsUser, value);
        }

        public static OptionMapping channel(GuildChannelUnion value) {
            return mapping(OptionType.CHANNEL, OptionMapping::getAsChannel, value);
        }

        public static OptionMapping role(Role value) {
            return mapping(OptionType.ROLE, OptionMapping::getAsRole, value);
        }

        public static OptionMapping mentionable(IMentionable value) {
            return mapping(OptionType.MENTIONABLE, OptionMapping::getAsMentionable, value);
        }

        public static OptionMapping number(Double value) {
            return mapping(OptionType.NUMBER, OptionMapping::getAsDouble, value);
        }

        public static OptionMapping attachment(Message.Attachment value) {
            return mapping(OptionType.ATTACHMENT, OptionMapping::getAsAttachment, value);
        }

        private static <T> OptionMapping mapping(OptionType type, Function<OptionMapping, T> methodCall, T value) {
            OptionMapping mapping = mock(OptionMapping.class);
            when(mapping.getType()).thenReturn(type);
            when(methodCall.apply(mapping)).thenReturn(value);
            return mapping;
        }
    }
}
