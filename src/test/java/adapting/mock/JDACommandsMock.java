package adapting.mock;

import com.github.kaktushose.jda.commands.JDAContext;
import com.github.kaktushose.jda.commands.SlashCommandUpdater;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.DispatcherSupervisor;
import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.middleware.MiddlewareRegistry;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;


public record JDACommandsMock(
        JDAContext jdaContext,
        DispatcherSupervisor dispatcherSupervisor,
        MiddlewareRegistry middlewareRegistry,
        TypeAdapterRegistry adapterRegistry,
        ValidatorRegistry validatorRegistry,
        DependencyInjector dependencyInjector,
        InteractionRegistry interactionRegistry,
        SlashCommandUpdater updater,
        RuntimeSupervisor runtimeSupervisor
) {
}
