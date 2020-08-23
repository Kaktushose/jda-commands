package com.github.kaktushose.jda.commands.api;

/**
 * An interface used to define a Provider of dependencies.
 *
 * <p>In order to use the dependency injection feature of this framework at least one Provider class is needed.
 * An instance of all Provider classes must be passed to {@link com.github.kaktushose.jda.commands.entities.JDACommandsBuilder#addProvider(Provider)}.
 *
 * <p>A Provider class is a class that contains methods that are annotated with {@link com.github.kaktushose.jda.commands.annotations.Produces}
 * and thus making objects available for dependency injection. All provided dependencies will be injected into fields annotated with
 * {@link com.github.kaktushose.jda.commands.annotations.Inject}. This does only work for classes annotated with
 * {@link com.github.kaktushose.jda.commands.annotations.CommandController}.
 *
 * <p>Please note that this is only a very basic implementation of dependency injection and should only be used inside command classes.
 *
 * @author Kaktushose
 * @version 1.0.0
 * @see com.github.kaktushose.jda.commands.annotations.Inject
 * @see com.github.kaktushose.jda.commands.annotations.Produces
 * @see com.github.kaktushose.jda.commands.annotations.CommandController
 * @see com.github.kaktushose.jda.commands.entities.JDACommandsBuilder
 * @since 1.0.0
 */
public interface Provider {
}
