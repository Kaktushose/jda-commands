package io.github.kaktushose.jdac.guice.internal;

import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.goldmensch.propane.event.Listener;
import dev.goldmensch.propane.property.PropertyProvider;
import io.github.kaktushose.jdac.JDACommands;
import io.github.kaktushose.jdac.dispatching.adapter.AdapterType;
import io.github.kaktushose.jdac.dispatching.adapter.TypeAdapter;
import io.github.kaktushose.jdac.dispatching.instance.Instantiator;
import io.github.kaktushose.jdac.dispatching.middleware.Middleware;
import io.github.kaktushose.jdac.dispatching.validation.Validator;
import io.github.kaktushose.jdac.guice.GuiceExtensionData;
import io.github.kaktushose.jdac.guice.Implementation;
import io.github.kaktushose.jdac.guice.internal.guice.GuiceExtensionModule;
import io.github.kaktushose.jdac.guice.internal.guice.RuntimeBoundScope;
import io.github.kaktushose.jdac.guice.internal.guice.modules.ConfigurationScopeModule;
import io.github.kaktushose.jdac.property.JDACIntrospection;
import io.github.kaktushose.jdac.property.JDACProperty;
import io.github.kaktushose.jdac.property.JDACPropertyProvider;
import io.github.kaktushose.jdac.property.events.RuntimeCloseEvent;
import io.github.kaktushose.jdac.property.extension.Extension;
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

/// The implementation of [Extension] for using Googles [Guice] as an [Instantiator].
///
/// Additionally, this extension allows the automatic registration of some types annotated with [`@Implementation`][Implementation].
/// For further information please see the docs on [`@Implementation`][Implementation].
///
/// @see GuiceExtensionData
@ApiStatus.Internal
public class GuiceExtension implements Extension<GuiceExtensionData> {

    private Injector injector;
    private final RuntimeBoundScope runtimeBoundScope = new RuntimeBoundScope();

    @Override
    public void init(@Nullable GuiceExtensionData data) {
        Injector found = data != null
                ? data.providedInjector()
                : Guice.createInjector();

        this.injector = found.createChildInjector(new GuiceExtensionModule());
    }

    @SuppressWarnings("unchecked")
    private <T> JDACPropertyProvider<T> provider(JDACProperty<?> type, Function<JDACIntrospection, ?> supplier) {
        return new JDACPropertyProvider<>((JDACProperty<T>) type, PropertyProvider.Priority.of(10), GuiceExtensionModule.class, (Function<JDACIntrospection, T>) supplier);
    }

    @Override
    public Collection<JDACPropertyProvider<?>> properties() {
        List<JDACPropertyProvider<?>> implementations = new ArrayList<>();

        implementations.add(provider(
                JDACProperty.INSTANTIATOR,
                _ -> new GuiceInstantiator(runtimeBoundScope, injector)
        ));

        addDynamicImplementations(implementations);
        return implementations;
    }

    private boolean shouldSkip(JDACProperty<?> property) {
        return property == JDACProperty.INSTANTIATOR
                || property == JDACProperty.CLASS_FINDER
                || property == JDACProperty.TYPE_ADAPTER
                || property == JDACProperty.MIDDLEWARE
                || property == JDACProperty.VALIDATOR;
    }

    private void addDynamicImplementations(List<JDACPropertyProvider<?>> list) {
        // load single types
        // TODO enable it again
//        for (JDACProperty<?> property : JDACProperty.LOADABLE) {
//            if (shouldSkip(property)) continue;
//
//            switch (property.generalized()) {
//                case MapProperty<?, ?> m -> throw new GuiceException("invalid-implementation", entry("class", m.valueType().getName()));
//                case CollectionProperty<?> e -> list.add(provider(property, ctx -> instances(ctx, Implementation.class, e.type()).toList()));
//                case SingleProperty<?> i -> list.add(provider(property, ctx -> {
//                    List<?> instances = instances(ctx, Implementation.class, i.type()).toList();
//                    if (instances.size() == 1) return instances.getFirst();
//                    if (instances.isEmpty()) return null;
//
//                    throw new GuiceException("multiple-instances",
//                            entry("type", i.type().getName()),
//                            entry("found", instances.stream().map(obj -> obj.getClass().getName()).collect(Collectors.joining(", ")))
//                    );
//                }));
//            }
//        }

        // load multiple implementable types
        list.add(provider(
                JDACProperty.TYPE_ADAPTER,
                ctx -> instances(ctx, Implementation.TypeAdapter.class, TypeAdapter.class)
                        .map(adapter -> {
                            Implementation.TypeAdapter ann = adapter.getClass().getAnnotation(Implementation.TypeAdapter.class);
                            return Map.entry(new AdapterType<>(Type.of(ann.source()), Type.of(ann.target())), adapter);
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
           )
        );

        list.add(provider(
                JDACProperty.VALIDATOR,
                ctx -> instances(ctx, Implementation.Validator.class, Validator.class)
                        .collect(Collectors.toMap(
                                instance -> instance.getClass().getAnnotation(Implementation.Validator.class).annotation(),
                                Function.identity())
                        )
        ));

        list.add(provider(
                JDACProperty.MIDDLEWARE,
                ctx -> instances(ctx, Implementation.Middleware.class, Middleware.class)
                        .map(middleware -> {
                            Implementation.Middleware ann = middleware.getClass().getAnnotation(Implementation.Middleware.class);
                            return Map.entry(ann.priority(), middleware);
                        })
                        .toList()
                )
        );
    }

    private <T> Stream<T> instances(JDACIntrospection ctx, Class<? extends Annotation> annotation, Class<T> type) {
        JDACIntrospection introspection = ctx.get(JDACProperty.INTROSPECTION);
        Injector childInjector = injector.createChildInjector(new ConfigurationScopeModule(introspection));

        return ctx.get(JDACProperty.MERGED_CLASS_FINDER)
                .search(annotation, type)
                .stream()
                .map(childInjector::getInstance);
    }

    @Override
    public void onStart(JDACommands commands) {
        JDACIntrospection introspection = commands.introspection();

        introspection.subscribe(Listener.create(RuntimeCloseEvent.class, (event, _) -> runtimeBoundScope.removeRuntime(event.runtimeId())));
    }

    @Override
    public Class<GuiceExtensionData> dataType() {
        return GuiceExtensionData.class;
    }
}
