package com.github.kaktushose.jda.commands.dispatching.refactor.handling;

import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.interactions.autocomplete.AutoCompleteEvent;
import com.github.kaktushose.jda.commands.dispatching.refactor.DispatcherContext;
import com.github.kaktushose.jda.commands.dispatching.refactor.Runtime;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.reflect.interactions.AutoCompleteDefinition;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;

import java.util.Optional;

public class AutoCompleteHandler extends EventHandler<com.github.kaktushose.jda.commands.dispatching.refactor.event.jda.AutoCompleteEvent> {

    public AutoCompleteHandler(DispatcherContext dispatcherContext) {
        super(dispatcherContext);
    }

    @Override
    public void accept(com.github.kaktushose.jda.commands.dispatching.refactor.event.jda.AutoCompleteEvent event, Runtime runtime) {
        CommandAutoCompleteInteraction interaction = event.event().getInteraction();
        var context = new Context(event.event(), interactionRegistry, implementationRegistry);

        Optional<AutoCompleteDefinition> optionalAutoComplete = interactionRegistry.getAutoCompletes().stream()
                .filter(it -> it.getCommandNames().stream().anyMatch(name -> interaction.getFullCommandName().startsWith(name)))
                .findFirst();

        if (optionalAutoComplete.isEmpty()) {
            log.debug("No auto complete handler found for {}", interaction.getFullCommandName());
            return;
        }

        AutoCompleteDefinition autoComplete = optionalAutoComplete.get();
        context.setInteractionDefinition(autoComplete);

        executeMiddlewares(context);
        if (checkCancelled(context)) {
            log.debug("Interaction execution cancelled by middleware");
            return;
        }

        log.debug("Input matches auto complete: {}", autoComplete.getDefinitionId());
        log.info("Executing auto complete {} for user {}", autoComplete.getMethod().getName(), interaction.getMember());
        try {
            autoComplete.getMethod().invoke(runtimeSupervisor.newRuntime(autoComplete).getInstance(), new AutoCompleteEvent(context, interactionRegistry));
        } catch (Exception exception) {
            throw new IllegalStateException("Auto complete execution failed!", exception);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private boolean checkCancelled(Context context) {
        if (context.isCancelled()) {
            ReplyContext replyContext = new ReplyContext(context);
            replyContext.getBuilder().applyData(context.getErrorMessage());
            replyContext.queue();
            return true;
        }
        return false;
    }
}
