package io.github.kaktushose.jdac.testing.invocation.internal;

import io.github.kaktushose.jdac.testing.TestScenario.Context;
import io.github.kaktushose.jdac.testing.invocation.AutoCompleteInvocation;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionType;

import static org.mockito.Mockito.*;

public abstract sealed class Invocation<T extends Interaction, R> permits AutoCompleteInvocation, ReplyableInvocation {

    protected final Context context;
    protected final T event;
    private User user;

    public Invocation(Context context, Class<T> eventClass, InteractionType interactionType) {
        this.context = context;

        event = mock(eventClass);

        lenient().when(event.getType()).thenReturn(interactionType);

        user = mock(User.class);
        when(event.getUser()).then((_) -> user);

        lenient().when(event.getUserLocale()).thenReturn(DiscordLocale.ENGLISH_US);
        lenient().when(event.getGuildLocale()).thenReturn(DiscordLocale.ENGLISH_US);
    }

    public Invocation<T, R> guildLocale(DiscordLocale locale) {
        when(event.getGuildLocale()).thenReturn(locale);
        return this;
    }

    public Invocation<T, R> userLocale(DiscordLocale locale) {
        when(event.getUserLocale()).thenReturn(locale);
        return this;
    }

    public Invocation<T, R> guild(Guild guild) {
        when(event.getGuild()).thenReturn(guild);
        return this;
    }

    public Invocation<T, R> member(Member member) {
        user = member.getUser();
        when(event.getMember()).thenReturn(member);
        return this;
    }

    public R invoke() {
        context.eventManager().handle((GenericInteractionCreateEvent) event);
        return complete();
    }

    protected abstract R complete();
}
