package io.github.kaktushose.jdac.configuration.internal;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.configuration.Property.Category;
import io.github.kaktushose.jdac.configuration.Property.Singleton;
import io.github.kaktushose.jdac.definitions.interactions.InteractionRegistry;
import io.github.kaktushose.jdac.dispatching.Runtime;
import io.github.kaktushose.jdac.dispatching.adapter.internal.TypeAdapters;
import io.github.kaktushose.jdac.dispatching.middleware.internal.Middlewares;
import io.github.kaktushose.jdac.embeds.internal.Embeds;
import io.github.kaktushose.jdac.internal.JDAContext;
import io.github.kaktushose.jdac.introspection.Stage;
import io.github.kaktushose.jdac.message.i18n.internal.BundleFinder;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface InternalProperties {

    Property<Embeds.Configuration> EMBED_CONFIG_INTERNAL = new Singleton<>("EMBED_CONFIG_INTERNAL", Category.PROVIDED
            , Embeds.Configuration.class, Stage.CONFIGURATION);

    Property<JDAContext> JDA_CONTEXT = new Singleton<>("JDA_CONTEXT", Category.PROVIDED, JDAContext.class,
                                                       Stage.CONFIGURATION);

    Property<Embeds> EMBEDS = new Singleton<>("EMBEDS", Category.PROVIDED, Embeds.class, Stage.CONFIGURATION);

    Property<Middlewares> MIDDLEWARES = new Singleton<>("MIDDLEWARES", Category.PROVIDED, Middlewares.class,
                                                        Stage.INITIALIZED);

    Property<TypeAdapters> TYPE_ADAPTERS = new Singleton<>("TYPE_ADAPTERS", Category.PROVIDED, TypeAdapters.class,
                                                           Stage.INITIALIZED);

    Property<InteractionRegistry> INTERACTION_REGISTRY = new Singleton<>("INTERACTION_REGISTRY", Category.PROVIDED,
                                                                         InteractionRegistry.class, Stage.INITIALIZED);

    Property<Runtime> RUNTIME = new Singleton<>("RUNTIME", Category.PROVIDED, Runtime.class, Stage.INTERACTION);

    Property<BundleFinder> BUNDLE_FINDER = new Singleton<>("BUNDLE_FINDER", Category.PROVIDED, BundleFinder.class,
                                                           Stage.CONFIGURATION);

}
