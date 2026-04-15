package io.github.kaktushose.jdac.property.internal;

import io.github.kaktushose.jdac.definitions.interactions.InteractionRegistry;
import io.github.kaktushose.jdac.dispatching.Runtime;
import io.github.kaktushose.jdac.dispatching.adapter.internal.TypeAdapters;
import io.github.kaktushose.jdac.dispatching.middleware.internal.Middlewares;
import io.github.kaktushose.jdac.embeds.internal.Embeds;
import io.github.kaktushose.jdac.internal.JDAContext;
import io.github.kaktushose.jdac.message.i18n.internal.BundleFinder;
import io.github.kaktushose.jdac.property.JDACProperty;
import io.github.kaktushose.jdac.property.JDACScope;
import dev.goldmensch.propane.property.Property;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface JDACInternalProperties {

    JDACProperty<Embeds.Configuration> EMBED_CONFIG_INTERNAL = new JDACSingletonProperty<>("EMBED_CONFIG_INTERNAL", Property.Source.PROVIDED, JDACScope.CONFIGURATION, Embeds.Configuration.class);

    JDACProperty<JDAContext> JDA_CONTEXT = new JDACSingletonProperty<>("JDA_CONTEXT", Property.Source.PROVIDED, JDACScope.CONFIGURATION, JDAContext.class);

    JDACProperty<Embeds> EMBEDS = new JDACSingletonProperty<>("EMBEDS", Property.Source.PROVIDED, JDACScope.CONFIGURATION, Embeds.class);

    JDACProperty<Middlewares> MIDDLEWARES = new JDACSingletonProperty<>("MIDDLEWARES", Property.Source.PROVIDED, JDACScope.INITIALIZED, Middlewares.class);

    JDACProperty<TypeAdapters> TYPE_ADAPTERS = new JDACSingletonProperty<>("TYPE_ADAPTERS", Property.Source.PROVIDED, JDACScope.INITIALIZED, TypeAdapters.class);

    JDACProperty<InteractionRegistry> INTERACTION_REGISTRY = new JDACSingletonProperty<>("INTERACTION_REGISTRY", Property.Source.PROVIDED, JDACScope.INITIALIZED, InteractionRegistry.class);

    JDACProperty<Runtime> RUNTIME = new JDACSingletonProperty<>("RUNTIME", Property.Source.PROVIDED, JDACScope.RUNTIME, Runtime.class);

    JDACProperty<BundleFinder> BUNDLE_FINDER = new JDACSingletonProperty<>("BUNDLE_FINDER", Property.Source.PROVIDED, JDACScope.CONFIGURATION, BundleFinder.class);

}
