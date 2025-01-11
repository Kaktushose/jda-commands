package adapting.mock;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapters;
import com.github.kaktushose.jda.commands.dispatching.JDAEventListener;
import com.github.kaktushose.jda.commands.dispatching.middleware.internal.Middlewares;
import com.github.kaktushose.jda.commands.dispatching.validation.internal.Validators;
import com.github.kaktushose.jda.commands.internal.JDAContext;
import com.github.kaktushose.jda.commands.internal.register.SlashCommandUpdater;


public record JDACommandsMock(
        JDAContext jdaContext,
        JDAEventListener JDAEventListener,
        Middlewares middlewareRegistry,
        TypeAdapters adapterRegistry,
        Validators validatorRegistry,
        DependencyInjector dependencyInjector,
        InteractionRegistry interactionRegistry,
        SlashCommandUpdater updater) {
}
