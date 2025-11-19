package io.github.kaktushose.jdac.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.kaktushose.jdac.configuration.ConfigurationContext;
import io.github.kaktushose.jdac.configuration.PropertyProvider;
import io.github.kaktushose.jdac.configuration.PropertyType;
import io.github.kaktushose.jdac.dispatching.instance.InteractionControllerInstantiator;
import io.github.kaktushose.jdac.configuration.Extension;
import io.github.kaktushose.jdac.guice.internal.GuiceExtensionModule;
import io.github.kaktushose.jdac.guice.internal.GuiceInteractionControllerInstantiator;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/// The implementation of [Extension] for using Google's [Guice] as an [InteractionControllerInstantiator].
///
/// Additionally, this extension allows the automatic registration of some types annotated with [`@Implementation`][io.github.kaktushose.jdac.guice.Implementation].
/// For further information please see the docs on [`@Implementation`][io.github.kaktushose.jdac.guice.Implementation].
///
/// @see GuiceExtensionData
@ApiStatus.Internal
public class GuiceExtension implements Extension<GuiceExtensionData> {

    private Injector injector;

    @Override
    public void init(@Nullable GuiceExtensionData data) {
        Injector found = data != null
                ? data.providedInjector()
                : Guice.createInjector();

        this.injector = found.createChildInjector(new GuiceExtensionModule());
    }

    private <T> PropertyProvider<T> property(PropertyType<T> type, Function<ConfigurationContext, T> supplier) {
        return new PropertyProvider<>(type, 10, supplier);
    }

    @Override
    public Collection<PropertyProvider<?>> properties() {
        List<PropertyProvider<?>> implementations = new ArrayList<>();

        implementations.add(property(
                PropertyType.INTERACTION_CONTROLLER_INSTANTIATOR,
                ctx -> new GuiceInteractionControllerInstantiator(ctx.get(PropertyType.I18N), injector)
        ));

//        addDynamicImplementations(implementations);
        return implementations;
    }

    @SuppressWarnings("unchecked")
//    private void addDynamicImplementations(List<PropertyProvider<?>> list) {
//        // load single types
//        for (var type : Implementation.ExtensionProvidable.class.getPermittedSubclasses()) {
//            if (type == Implementation.ProvidableContainer.class || type == InteractionControllerInstantiator.class || type == ClassFinder.class) continue;
//            list.add(property
//                    (Class<Implementation.ExtensionProvidable>) type,
//                    builder -> instances(builder, io.github.kaktushose.jdac.guice.Implementation.class, type)
//                            .map(instance -> (Implementation.ExtensionProvidable) instance)
//                            .toList()
//            ));
//        }
//
//        // load multiple implementable types
//        list.add(new Implementation<>(
//                TypeAdapterContainer.class, builder -> instances(builder, io.github.kaktushose.jdac.guice.Implementation.TypeAdapter.class, TypeAdapter.class)
//                .map(adapter -> new TypeAdapterContainer(
//                        Type.of(adapter.getClass().getAnnotation(io.github.kaktushose.jdac.guice.Implementation.TypeAdapter.class).source()),
//                        Type.of(adapter.getClass().getAnnotation(io.github.kaktushose.jdac.guice.Implementation.TypeAdapter.class).target()),
//                        adapter)
//                ).toList()
//        ));
//
//        list.add(new Implementation<>(
//                ValidatorContainer.class, builder -> instances(builder, io.github.kaktushose.jdac.guice.Implementation.Validator.class, Validator.class)
//                .map(validator -> new ValidatorContainer(
//                        validator.getClass().getAnnotation(io.github.kaktushose.jdac.guice.Implementation.Validator.class).annotation(),
//                        validator)
//                ).toList()
//        ));
//
//        list.add(new Implementation<>(MiddlewareContainer.class, builder -> instances(builder, io.github.kaktushose.jdac.guice.Implementation.Middleware.class, Middleware.class)
//                .map(middleware -> new MiddlewareContainer(
//                        middleware.getClass().getAnnotation(io.github.kaktushose.jdac.guice.Implementation.Middleware.class).priority(),
//                        middleware)
//                ).toList()
//        ));
//    }

//    private <T> Stream<T> instances(JDACBuilderDataOld builder, Class<? extends Annotation> annotation, Class<T> type) {
//        return builder.mergedClassFinder()
//                .search(annotation, type)
//                .stream()
//                .map(injector::getInstance);
//    }

    @Override
    public Class<GuiceExtensionData> dataType() {
        return GuiceExtensionData.class;
    }
}
