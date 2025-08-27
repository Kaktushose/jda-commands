package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapters;
import com.github.kaktushose.jda.commands.dispatching.expiration.ExpirationStrategy;
import com.github.kaktushose.jda.commands.dispatching.handling.EventHandler;
import com.github.kaktushose.jda.commands.dispatching.instance.InteractionControllerInstantiator;
import com.github.kaktushose.jda.commands.dispatching.middleware.internal.Middlewares;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.embeds.internal.Embeds;
import com.github.kaktushose.jda.commands.i18n.I18n;
import org.jetbrains.annotations.ApiStatus;

/// A collection of classes relevant for [EventHandler]s.
@ApiStatus.Internal
public record DispatchingContext(Middlewares middlewares,
                                 ErrorMessageFactory errorMessageFactory,
                                 InteractionRegistry registry,
                                 TypeAdapters adapterRegistry,
                                 ExpirationStrategy expirationStrategy,
                                 InteractionControllerInstantiator instanceProvider,
                                 InteractionDefinition.ReplyConfig globalReplyConfig,
                                 Embeds embeds,
                                 I18n i18n) {
}
