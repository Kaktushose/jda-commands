package io.github.kaktushose.jdac.testing.invocation;

import io.github.kaktushose.jdac.testing.TestScenario.Context;
import io.github.kaktushose.jdac.testing.invocation.internal.Invocation;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.InteractionType;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.interactions.AutoCompleteCallbackAction;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

public final class AutoCompleteInvocation extends Invocation<CommandAutoCompleteInteractionEvent, List<Choice>> {

    private final CompletableFuture<List<Choice>> result;
    private final AutoCompleteQuery focusedOption;
    private String value;

    public AutoCompleteInvocation(Context context, String command, String option) {
        super(context, CommandAutoCompleteInteractionEvent.class, InteractionType.COMMAND_AUTOCOMPLETE);

        value = "";

        focusedOption = mock(AutoCompleteQuery.class);
        lenient().when(event.getFocusedOption()).thenReturn(focusedOption);
        lenient().when(focusedOption.getName()).thenReturn(option);
        lenient().when(focusedOption.getValue()).thenReturn(value);

        CommandAutoCompleteInteraction interaction = mock(CommandAutoCompleteInteraction.class);
        when(event.getInteraction()).thenReturn(interaction);
        when(interaction.getFullCommandName()).thenReturn(command);

        result = new CompletableFuture<>();
        doAnswer(invocation -> {
            result.complete(invocation.getArgument(0));
            return mock(AutoCompleteCallbackAction.class);
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
        when(focusedOption.getType()).thenReturn(type);
        return this;
    }

    public AutoCompleteInvocation value(String value) {
        this.value = value;
        return this;
    }
}
