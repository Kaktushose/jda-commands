package adapting.mock;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.JDAEventListener;
import com.github.kaktushose.jda.commands.dispatching.middleware.MiddlewareRegistry;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.internal.JDAContext;
import com.github.kaktushose.jda.commands.internal.register.SlashCommandUpdater;


public record JDACommandsMock(
        JDAContext jdaContext,
        JDAEventListener JDAEventListener,
        MiddlewareRegistry middlewareRegistry,
        TypeAdapterRegistry adapterRegistry,
        ValidatorRegistry validatorRegistry,
        DependencyInjector dependencyInjector,
        InteractionRegistry interactionRegistry,
        SlashCommandUpdater updater) {
}
