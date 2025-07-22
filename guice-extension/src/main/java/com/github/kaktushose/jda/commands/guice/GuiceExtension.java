package com.github.kaktushose.jda.commands.guice;

import com.github.kaktushose.jda.commands.definitions.description.ClassFinder;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.instance.InteractionControllerInstantiator;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.extension.Extension;
import com.github.kaktushose.jda.commands.extension.Implementation;
import com.github.kaktushose.jda.commands.extension.Implementation.MiddlewareContainer;
import com.github.kaktushose.jda.commands.extension.Implementation.TypeAdapterContainer;
import com.github.kaktushose.jda.commands.extension.Implementation.ValidatorContainer;
import com.github.kaktushose.jda.commands.extension.JDACBuilderData;
import com.github.kaktushose.jda.commands.guice.internal.GuiceExtensionModule;
import com.github.kaktushose.jda.commands.guice.internal.GuiceInteractionControllerInstantiator;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.kaktushose.proteus.type.Type;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/// The implementation of [Extension] for using Google's [Guice] as an [InteractionControllerInstantiator].
///
/// Additionally, this extension allows the automatic registration of some types annotated with [`@Implementation`][com.github.kaktushose.jda.commands.guice.Implementation].
/// For further information please see the docs on [`@Implementation`][com.github.kaktushose.jda.commands.guice.Implementation].
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

    
    @Override
    public Collection<Implementation<?>> providedImplementations() {
        List<Implementation<?>> implementations = new ArrayList<>();

        implementations.add(Implementation.single(
                InteractionControllerInstantiator.class,
                data -> new GuiceInteractionControllerInstantiator(data.i18n(), injector)
        ));

        addDynamicImplementations(implementations);
        return implementations;
    }

    @SuppressWarnings("unchecked")
    private void addDynamicImplementations(List<Implementation<?>> list) {
        // load single types
        for (var type : Implementation.ExtensionProvidable.class.getPermittedSubclasses()) {
            if (type == Implementation.ProvidableContainer.class || type == InteractionControllerInstantiator.class || type == ClassFinder.class) continue;
            list.add(new Implementation<>(
                    (Class<Implementation.ExtensionProvidable>) type,
                    builder -> instances(builder, com.github.kaktushose.jda.commands.guice.Implementation.class, type)
                            .map(instance -> (Implementation.ExtensionProvidable) instance)
                            .toList()
            ));
        }

        // load multiple implementable types
        list.add(new Implementation<>(
                TypeAdapterContainer.class, builder -> instances(builder, com.github.kaktushose.jda.commands.guice.Implementation.TypeAdapter.class, TypeAdapter.class)
                .map(adapter -> new TypeAdapterContainer(
                        Type.of(adapter.getClass().getAnnotation(com.github.kaktushose.jda.commands.guice.Implementation.TypeAdapter.class).source()),
                        Type.of(adapter.getClass().getAnnotation(com.github.kaktushose.jda.commands.guice.Implementation.TypeAdapter.class).target()),
                        adapter)
                ).toList()
        ));

        list.add(new Implementation<>(
                ValidatorContainer.class, builder -> instances(builder, com.github.kaktushose.jda.commands.guice.Implementation.Validator.class, Validator.class)
                .map(validator -> new ValidatorContainer(
                        validator.getClass().getAnnotation(com.github.kaktushose.jda.commands.guice.Implementation.Validator.class).annotation(),
                        validator)
                ).toList()
        ));

        list.add(new Implementation<>(MiddlewareContainer.class, builder -> instances(builder, com.github.kaktushose.jda.commands.guice.Implementation.Middleware.class, Middleware.class)
                .map(middleware -> new MiddlewareContainer(
                        middleware.getClass().getAnnotation(com.github.kaktushose.jda.commands.guice.Implementation.Middleware.class).priority(),
                        middleware)
                ).toList()
        ));
    }

    private <T> Stream<T> instances(JDACBuilderData builder, Class<? extends Annotation> annotation, Class<T> type) {
        return builder.mergedClassFinder()
                .search(annotation, type)
                .stream()
                .map(injector::getInstance);
    }

    @Override
    public Class<GuiceExtensionData> dataType() {
        return GuiceExtensionData.class;
    }
}
