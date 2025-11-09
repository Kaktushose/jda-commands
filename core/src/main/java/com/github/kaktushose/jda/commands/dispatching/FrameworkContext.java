package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.definitions.interactions.command.CommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapters;
import com.github.kaktushose.jda.commands.dispatching.expiration.ExpirationStrategy;
import com.github.kaktushose.jda.commands.dispatching.instance.InteractionControllerInstantiator;
import com.github.kaktushose.jda.commands.dispatching.middleware.internal.Middlewares;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.embeds.internal.Embeds;
import com.github.kaktushose.jda.commands.message.MessageResolver;
import com.github.kaktushose.jda.commands.message.i18n.I18n;
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
