package io.github.kaktushose.jdac.dispatching;

import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.InteractionRegistry;
import io.github.kaktushose.jdac.definitions.interactions.command.CommandDefinition;
import io.github.kaktushose.jdac.dispatching.adapter.internal.TypeAdapters;
import io.github.kaktushose.jdac.dispatching.expiration.ExpirationStrategy;
import io.github.kaktushose.jdac.dispatching.instance.InteractionControllerInstantiator;
import io.github.kaktushose.jdac.dispatching.middleware.internal.Middlewares;
import io.github.kaktushose.jdac.embeds.error.ErrorMessageFactory;
import io.github.kaktushose.jdac.embeds.internal.Embeds;
import io.github.kaktushose.jdac.message.MessageResolver;
import io.github.kaktushose.jdac.message.i18n.I18n;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public record FrameworkContext(
        Middlewares middlewares,
        ErrorMessageFactory errorMessageFactory,
        InteractionRegistry interactionRegistry,
        TypeAdapters adapterRegistry,
        ExpirationStrategy expirationStrategy,
        InteractionControllerInstantiator instanceProvider,
        Embeds embeds,
        I18n i18n,
        MessageResolver messageResolver,
        InteractionDefinition.ReplyConfig globalReplyConfig,
        CommandDefinition.CommandConfig globalCommandConfig
) {
}
