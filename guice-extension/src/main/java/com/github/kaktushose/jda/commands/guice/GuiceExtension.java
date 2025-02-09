package com.github.kaktushose.jda.commands.guice;

import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.instance.InteractionControllerInstantiator;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.extension.Extension;
import com.github.kaktushose.jda.commands.extension.Implementation;
import com.github.kaktushose.jda.commands.extension.Implementation.MiddlewareContainer;
import com.github.kaktushose.jda.commands.extension.Implementation.TypeAdapterContainer;
import com.github.kaktushose.jda.commands.extension.Implementation.ValidatorContainer;
import com.github.kaktushose.jda.commands.extension.JDACBuilderData;
import com.github.kaktushose.jda.commands.guice.internal.GuiceExtensionModule;
import com.github.kaktushose.jda.commands.guice.internal.GuiceInteractionControllerInstantiator;
import com.github.kaktushose.jda.commands.permissions.PermissionsProvider;
import com.github.kaktushose.jda.commands.scope.GuildScopeProvider;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
public class GuiceExtension implements Extension {

    private static final Class<com.github.kaktushose.jda.commands.guice.Implementation> IMPLEMENTATION_ANN =
            com.github.kaktushose.jda.commands.guice.Implementation.class;
    private final List<Class<? extends Implementation.ExtensionProvidable>> loadableClasses = List.of(
            Descriptor.class,
            ErrorMessageFactory.class,
            PermissionsProvider.class,
            GuildScopeProvider.class
    );
    private Injector injector;

    @Override
    public void init(@Nullable Data data) {
        Injector found = data != null
                ? ((GuiceExtensionData) data).providedInjector()
                : Guice.createInjector();

        this.injector = found.createChildInjector(new GuiceExtensionModule());
    }

    @NotNull
    @Override
    public Collection<Implementation<?>> providedImplementations() {
        List<Implementation<?>> implementations = new ArrayList<>();

        implementations.add(Implementation.single(
                InteractionControllerInstantiator.class,
                _ -> new GuiceInteractionControllerInstantiator(injector)
        ));

        addDynamicImplementations(implementations);
        return implementations;
    }

    @SuppressWarnings("unchecked")
    private void addDynamicImplementations(List<Implementation<?>> list) {
        // load single types
        for (var type : loadableClasses) {
            list.add(new Implementation<>(
                    (Class<Implementation.ExtensionProvidable>) type,
                    builder -> instances(builder, type)
                            .map(instance -> (Implementation.ExtensionProvidable) instance)
                            .toList()
            ));
        }

        // load multiple implementable types
        list.add(new Implementation<>(
                TypeAdapterContainer.class, builder -> instances(builder, TypeAdapter.class)
                .map(adapter -> new TypeAdapterContainer(
                        adapter.getClass().getAnnotation(IMPLEMENTATION_ANN).clazz(),
                        adapter)
                ).toList()
        ));

        list.add(new Implementation<>(
                ValidatorContainer.class, builder -> instances(builder, Validator.class)
                .map(validator -> new ValidatorContainer(
                        validator.getClass().getAnnotation(IMPLEMENTATION_ANN).annotation(),
                        validator)
                ).toList()
        ));

        list.add(new Implementation<>(MiddlewareContainer.class, builder -> instances(builder, Middleware.class)
                .map(middleware -> new MiddlewareContainer(
                        middleware.getClass().getAnnotation(IMPLEMENTATION_ANN).priority(),
                        middleware)
                ).toList()
        ));
    }

    private <T> Stream<T> instances(JDACBuilderData builder, Class<T> type) {
        return builder.mergedClassFinder()
                .search(IMPLEMENTATION_ANN, type)
                .stream()
                .map(injector::getInstance);
    }

    @Override
    public @NotNull Class<GuiceExtensionData> dataType() {
        return GuiceExtensionData.class;
    }

    @NotNull
    public Injector injector() {
        return injector;
    }
}
