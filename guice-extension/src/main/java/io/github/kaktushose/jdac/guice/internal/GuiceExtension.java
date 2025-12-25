package io.github.kaktushose.jdac.guice.internal;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.kaktushose.jdac.JDACommands;
import io.github.kaktushose.jdac.configuration.Extension;
import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.configuration.PropertyProvider;
import io.github.kaktushose.jdac.dispatching.adapter.AdapterType;
import io.github.kaktushose.jdac.dispatching.adapter.TypeAdapter;
import io.github.kaktushose.jdac.dispatching.instance.InteractionControllerInstantiator;
import io.github.kaktushose.jdac.dispatching.middleware.Middleware;
import io.github.kaktushose.jdac.dispatching.validation.Validator;
import io.github.kaktushose.jdac.guice.GuiceExtensionData;
import io.github.kaktushose.jdac.guice.Implementation;
import io.github.kaktushose.jdac.guice.internal.guice.GuiceExtensionModule;
import io.github.kaktushose.jdac.guice.internal.guice.RuntimeBoundScope;
import io.github.kaktushose.jdac.introspection.Introspection;
import io.github.kaktushose.jdac.introspection.lifecycle.events.RuntimeCloseEvent;
import io.github.kaktushose.proteus.type.Type;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

/// The implementation of [Extension] for using Googles [Guice] as an [InteractionControllerInstantiator].
///
/// Additionally, this extension allows the automatic registration of some types annotated with [`@Implementation`][Implementation].
/// For further information please see the docs on [`@Implementation`][Implementation].
///
/// @see GuiceExtensionData
@ApiStatus.Internal
public class GuiceExtension implements Extension<GuiceExtensionData> {

    @Nullable
    private Injector injector;
    private RuntimeBoundScope runtimeBoundScope = new RuntimeBoundScope();

    @Override
    public void init(@Nullable GuiceExtensionData data) {
        Injector found = data != null
                ? data.providedInjector()
                : Guice.createInjector();

        this.injector = found.createChildInjector(new GuiceExtensionModule());
    }

    @SuppressWarnings("unchecked")
    private <T> PropertyProvider<T> provider(Property<?> type, Function<PropertyProvider.Context, ?> supplier) {
        return PropertyProvider.create((Property<T>) type, 10, (Function<PropertyProvider.Context, T>) supplier);
    }

    @Override
    public Collection<PropertyProvider<?>> properties() {
        List<PropertyProvider<?>> implementations = new ArrayList<>();

        implementations.add(provider(
                Property.INTERACTION_CONTROLLER_INSTANTIATOR,
                _ -> new GuiceInteractionControllerInstantiator(runtimeBoundScope, injector)
        ));

        addDynamicImplementations(implementations);
        return implementations;
    }

    private boolean shouldSkip(Property<?> property) {
        return property == Property.INTERACTION_CONTROLLER_INSTANTIATOR
                || property == Property.CLASS_FINDER
                || property == Property.TYPE_ADAPTER
                || property == Property.MIDDLEWARE
                || property == Property.VALIDATOR;
    }

    private void addDynamicImplementations(List<PropertyProvider<?>> list) {
        // load single types
        for (var property : Property.LOADABLE) {
            if (shouldSkip(property)) continue;

            switch (property) {
                case Property.Map<?, ?> m -> throw new GuiceException("invalid-implementation", entry("class", m.value().getName()));
                case Property.Enumeration<?> e -> list.add(provider(e, ctx -> instances(ctx, Implementation.class, e.type()).toList()));
                case Property.Singleton<?> i -> list.add(provider(i, ctx -> {
                    List<?> instances = instances(ctx, Implementation.class, i.type()).toList();
                    if (instances.size() == 1) return instances.getFirst();
                    if (instances.isEmpty()) return null;

                    throw new GuiceException("multiple-instances",
                            entry("type", i.type().getName()),
                            entry("found", instances.stream().map(obj -> obj.getClass().getName()).collect(Collectors.joining(", ")))
                    );
                }));
            }
        }

        // load multiple implementable types
        list.add(provider(
                Property.TYPE_ADAPTER,
                ctx -> instances(ctx, Implementation.TypeAdapter.class, TypeAdapter.class)
                        .map(adapter -> {
                            Implementation.TypeAdapter ann = adapter.getClass().getAnnotation(Implementation.TypeAdapter.class);
                            return Map.entry(new AdapterType<>(Type.of(ann.source()), Type.of(ann.target())), adapter);
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
           )
        );

        list.add(provider(
                Property.VALIDATOR,
                ctx -> instances(ctx, Implementation.Validator.class, Validator.class)
                        .collect(Collectors.toMap(
                                instance -> instance.getClass().getAnnotation(Implementation.Validator.class).annotation(),
                                Function.identity())
                        )
        ));

        list.add(provider(
                Property.MIDDLEWARE,
                ctx -> instances(ctx, Implementation.Middleware.class, Middleware.class)
                        .map(middleware -> {
                            Implementation.Middleware ann = middleware.getClass().getAnnotation(Implementation.Middleware.class);
                            return Map.entry(ann.priority(), middleware);
                        })
                        .toList()
                )
        );
    }

    private <T> Stream<T> instances(PropertyProvider.Context ctx, Class<? extends Annotation> annotation, Class<T> type) {
        return ctx.get(Property.MERGED_CLASS_FINDER)
                .search(annotation, type)
                .stream()
                .map(injector::getInstance);
    }

    @Override
    public void onStart(JDACommands commands) {
        Introspection introspection = commands.introspection();

        introspection.subscribe(RuntimeCloseEvent.class, (event, _) -> runtimeBoundScope.removeRuntime(event.runtimeId()));
    }

    @Override
    public Class<GuiceExtensionData> dataType() {
        return GuiceExtensionData.class;
    }
}
