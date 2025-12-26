package io.github.kaktushose.jdac.introspection.internal;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.configuration.internal.InternalProperties;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.InteractionRegistry;
import io.github.kaktushose.jdac.dispatching.Runtime;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.embeds.internal.Embeds;
import io.github.kaktushose.jdac.message.MessageResolver;
import io.github.kaktushose.jdac.message.i18n.I18n;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;

import static io.github.kaktushose.jdac.introspection.Introspection.accGet;

@ApiStatus.Internal
public class IntroAccess {
    public static InvocationContext<?> accInvocationContext() {
        return accGet(Property.INVOCATION_CONTEXT);
    }

    public static Runtime accRuntime() {
        return accGet(InternalProperties.RUNTIME);
    }

    public static Locale accUserLocale() {
        return accInvocationContext().event().getUserLocale().toLocale();
    }

    public static GenericInteractionCreateEvent accJdaEvent() {
        return accInvocationContext().event();
    }

    public static InteractionDefinition.ReplyConfig accReplyConfig() {
        return accInvocationContext().replyConfig();
    }

    public static InteractionRegistry accInteractionRegistry() {
        return accGet(InternalProperties.INTERACTION_REGISTRY);
    }

    public static Embeds accEmbeds() {
        return accGet(InternalProperties.EMBEDS);
    }

    public static MessageResolver accMessageResolver() {
        return accGet(Property.MESSAGE_RESOLVER);
    }

    public static I18n accI18n() {
        return accGet(Property.I18N);
    }
}
