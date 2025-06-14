package internal.invocation;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class SlashCommandInvocation {

    private final IEventManager eventManager;
    private final SlashCommandInteractionEvent event;
    private final CompletableFuture<MessageCreateData> reply = new CompletableFuture<>();
    private final Map<String, OptionMapping> optionMappings = new HashMap<>();
    private User user;

    public SlashCommandInvocation(IEventManager eventManager, String command) {
        this.eventManager = eventManager;

        event = mock(SlashCommandInteractionEvent.class);
        when(event.getFullCommandName()).thenReturn(command);
        when(event.getOption(anyString())).then(invocation -> optionMappings.get((String) invocation.getArgument(0)));
        when(event.deferReply(anyBoolean())).thenReturn(mock(ReplyCallbackAction.class));

        user = mock(User.class);
        when(event.getUser()).then((_) -> user);

        InteractionHook hook = mock(InteractionHook.class);
        when(event.getHook()).thenReturn(hook);
        lenient().when(hook.sendMessage(any(MessageCreateData.class))).then(invocation -> {
            reply.complete(MessageCreateData.fromEditData(invocation.getArgument(0)));
            return mock(WebhookMessageEditAction.class);
        });
        lenient().when(hook.editOriginal(any(MessageEditData.class))).then(invocation -> {
            reply.complete(MessageCreateData.fromEditData(invocation.getArgument(0)));
            return mock(WebhookMessageEditAction.class);
        });

        lenient().when(event.getUserLocale()).thenReturn(DiscordLocale.ENGLISH_US);
        lenient().when(event.getGuildLocale()).thenReturn(DiscordLocale.ENGLISH_US);
    }

    public SlashCommandInvocation option(String name, OptionMapping mapping) {
        optionMappings.put(name, mapping);
        return this;
    }

    public SlashCommandInvocation channel(MessageChannelUnion channel) {
        when(event.getChannel()).thenReturn(channel);
        return this;
    }

    public SlashCommandInvocation guildLocale(DiscordLocale locale) {
        when(event.getGuildLocale()).thenReturn(locale);
        return this;
    }

    public SlashCommandInvocation userLocale(DiscordLocale locale) {
        when(event.getUserLocale()).thenReturn(locale);
        return this;
    }

    public SlashCommandInvocation guild(Guild guild) {
        when(event.getGuild()).thenReturn(guild);
        return this;
    }

    public SlashCommandInvocation member(Member member) {
        user = member.getUser();
        when(event.getMember()).thenReturn(member);
        return this;
    }

    public CompletableFuture<MessageCreateData> invoke() {
        eventManager.handle(event);
        return reply;
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
