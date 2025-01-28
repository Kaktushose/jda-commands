package com.github.kaktushose.jda.commands.guice;

import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.instance.InteractionClassProvider;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.extension.Extension;
import com.github.kaktushose.jda.commands.extension.Implementation;
import com.github.kaktushose.jda.commands.extension.ReadonlyJDACBuilder;
import com.github.kaktushose.jda.commands.guice.internal.GuiceExtensionModule;
import com.github.kaktushose.jda.commands.guice.internal.GuiceInteractionClassProvider;
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

/// The implementation of [Extension] for using Google's [Guice] as an [InteractionClassProvider].
///
/// @see GuiceExtensionData
@ApiStatus.Internal
public class GuiceExtension implements Extension {

    private static final Class<com.github.kaktushose.jda.commands.guice.Implementation> IMPLEMENTATION_ANN =
            com.github.kaktushose.jda.commands.guice.Implementation.class;

    private Injector injector;

    @Override
    public void init(@Nullable Data data) {
        Injector found = data != null
                ? ((GuiceExtensionData) data).providedInjector()
                : Guice.createInjector();

        this.injector = found.createChildInjector(new GuiceExtensionModule());
    }

    @Override
    public @NotNull Collection<Implementation<?>> providedImplementations() {
        List<Implementation<?>> implementations = new ArrayList<>();

        implementations.add(Implementation.single(
                InteractionClassProvider.class,
                _ -> new GuiceInteractionClassProvider(this)));

        addDynamicImplementations(implementations);
        return implementations;
    }

    private final List<Class<? extends Implementation.ExtensionImplementable>> loadableClasses = List.of(
            Descriptor.class,
            ErrorMessageFactory.class,
            PermissionsProvider.class,
            GuildScopeProvider.class
    );

    @SuppressWarnings("unchecked")
    private void addDynamicImplementations(List<Implementation<?>> list) {
        // load single types
        for (var type : loadableClasses) {
            list.add(new Implementation<>(
                    (Class<Implementation.ExtensionImplementable>) type,
                    builder -> searchImplementedClasses(builder, type)
                                .map(instance -> (Implementation.ExtensionImplementable) instance)
                                .toList()
            ));
        }

        // load multiple implementable types
        list.add(new Implementation<>(
                Implementation.TypeAdapterContainer.class,
                builder -> searchImplementedClasses(builder, TypeAdapter.class)
                        .map(adapter -> new Implementation.TypeAdapterContainer(
                                adapter.getClass().getAnnotation(IMPLEMENTATION_ANN).clazz(),
                                adapter)
                        )
                        .toList()
        ));

        list.add(new Implementation<>(
                Implementation.ValidatorContainer.class,
                builder -> searchImplementedClasses(builder, Validator.class)
                        .map(validator -> new Implementation.ValidatorContainer(
                                validator.getClass().getAnnotation(IMPLEMENTATION_ANN).annotation(),
                                validator)
                        )
                        .toList()
        ));

        list.add(new Implementation<>(
                Implementation.MiddlewareContainer.class,
                builder -> searchImplementedClasses(builder, Middleware.class)
                        .map(middleware -> new Implementation.MiddlewareContainer(
                                middleware.getClass().getAnnotation(IMPLEMENTATION_ANN).priority(),
                                middleware)
                        )
                        .toList()
        ));
    }

    private <T> Stream<T> searchImplementedClasses(ReadonlyJDACBuilder builder, Class<T> type) {
        return builder
                .mergedClassFinder()
                .search(IMPLEMENTATION_ANN, type)
                .stream()
                .map(injector::getInstance);
    }

    public static class JDACGuiceException extends RuntimeException {
        private JDACGuiceException(String message) {
            super(message);
        }
    }

    @Override
    public @NotNull Class<GuiceExtensionData> dataType() {
        return GuiceExtensionData.class;
    }

    public Injector injector() {
        return injector;
    }
}
