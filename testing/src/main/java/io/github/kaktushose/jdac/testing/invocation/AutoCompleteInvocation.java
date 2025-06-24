package io.github.kaktushose.jdac.testing.invocation;

import com.github.kaktushose.jda.commands.dispatching.events.interactions.AutoCompleteEvent;
import io.github.kaktushose.jdac.testing.TestScenario.Context;
import io.github.kaktushose.jdac.testing.invocation.internal.Invocation;
import net.dv8tion.jda.api.interactions.InteractionType;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

public final class AutoCompleteInvocation extends Invocation<AutoCompleteEvent, List<Choice>> {

    private final CompletableFuture<List<Choice>> result;
    private String value;

    public AutoCompleteInvocation(Context context, String command, String option) {
        super(context, AutoCompleteEvent.class, InteractionType.COMMAND_AUTOCOMPLETE);

        value = "";
        when(event.getValue()).thenReturn(value);

        CommandAutoCompleteInteraction interaction = mock(CommandAutoCompleteInteraction.class);
        when(interaction.getFullCommandName()).thenReturn(command);
        when(event.getName()).thenReturn(option);

        result = new CompletableFuture<>();
        doAnswer(invocation -> {
            result.complete(invocation.getArgument(0));
            return null;
        }).when(event).replyChoices(anyCollection());
    }

    @Override
    protected List<Choice> complete() {
        try {
            return result.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public AutoCompleteInvocation optionType(OptionType type) {
        when(event.getOptionType()).thenReturn(type);
        return this;
    }

    public AutoCompleteInvocation value(String value) {
        this.value = value;
        return this;
    }
}
