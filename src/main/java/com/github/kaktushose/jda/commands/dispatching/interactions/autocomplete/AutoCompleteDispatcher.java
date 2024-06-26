package com.github.kaktushose.jda.commands.dispatching.interactions.autocomplete;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericDispatcher;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.reflect.interactions.AutoCompleteDefinition;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Dispatches auto complete by taking a {@link Context} and passing it through the execution chain.
 *
 * @since 4.0.0
 */
public class AutoCompleteDispatcher extends GenericDispatcher {

    private static final Logger log = LoggerFactory.getLogger(AutoCompleteDispatcher.class);

    /**
     * Constructs a new AutoCompleteDispatcher.
     *
     * @param jdaCommands the corresponding {@link JDACommands} instance.
     */
    public AutoCompleteDispatcher(JDACommands jdaCommands) {
        super(jdaCommands);
    }

    @Override
    public void onEvent(Context context) {
        CommandAutoCompleteInteractionEvent event = (CommandAutoCompleteInteractionEvent) context.getEvent();
        Optional<AutoCompleteDefinition> optionalAutoComplete = interactionRegistry.getAutoCompletes().stream()
                .filter(it -> it.getCommandNames().contains(event.getFullCommandName()))
                .findFirst();

        if (optionalAutoComplete.isEmpty()) {
            log.debug("No auto complete handler found for {}", event.getFullCommandName());
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
        log.info("Executing auto complete {} for user {}", autoComplete.getMethod().getName(), event.getMember());
        try {
            autoComplete.getMethod().invoke(runtimeSupervisor.newRuntime(autoComplete).getInstance(), new AutoCompleteEvent(context));
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
