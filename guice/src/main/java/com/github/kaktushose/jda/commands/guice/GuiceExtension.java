package com.github.kaktushose.jda.commands.guice;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.dispatching.instance.InteractionClassProvider;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.extension.Extension;
import com.github.kaktushose.jda.commands.extension.Implementation;
import com.github.kaktushose.jda.commands.guice.internal.GuiceInteractionClassProvider;
import com.github.kaktushose.jda.commands.permissions.PermissionsProvider;
import com.github.kaktushose.jda.commands.scope.GuildScopeProvider;
import com.google.inject.Guice;
import com.google.inject.Injector;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SequencedCollection;
import java.util.stream.Collectors;

/// The implementation of [Extension] for using Google's [Guice] as an [InteractionClassProvider].
///
/// @see GuiceExtensionData
public class GuiceExtension implements Extension {

    private static final Logger log = LoggerFactory.getLogger(GuiceExtension.class);
    private JDACommands jdaCommands;
    private Injector injector;

    @Override
    public void init(@Nullable Data data) {
        this.injector = data != null
                ? ((GuiceExtensionData) data).providedInjector()
                : Guice.createInjector();
    }

    @Override
    public @NotNull Collection<Implementation<?>> providedImplementations() {
        List<Implementation<?>> implementations = new ArrayList<>();
        implementations.add(new Implementation<>(
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
        for (var type : loadableClasses) {
            list.add(new Implementation<>(
                    (Class<Implementation.ExtensionImplementable>) type,
                    builder -> {
                        SequencedCollection<? extends Class<? extends Implementation.ExtensionImplementable>> classes = builder
                                .mergedClassFinder()
                                .search(Singleton.class, type);
                        if (classes.isEmpty()) {
                            return null;
                        }

                        if (classes.size() == 1) {
                            log.debug("Found {} implementation provided by class annotated with @Singleton: {}", type, classes.getFirst());
                            return injector.getInstance(classes.getFirst());
                        }

                        throw new JDACGuiceException("Multiple instances of interface %s found annotated with @Singleton. There can be only one implementation!: \n%s".formatted(
                                type, classes.stream()
                                        .map(Class::getName)
                                        .collect(Collectors.joining(System.lineSeparator()))
                        ));
                    }
            ));
        }
    }

    public static class JDACGuiceException extends RuntimeException {
        private JDACGuiceException(String message) {
            super(message);
        }
    }

    @Override
    public void afterFrameworkInit(@NotNull JDACommands jdaCommands) {
        this.jdaCommands = jdaCommands;
    }

    @Override
    public @NotNull Class<GuiceExtensionData> dataType() {
        return GuiceExtensionData.class;
    }

    public Injector injector() {
        return injector;
    }

    public JDACommands jdaCommands() {
        return jdaCommands;
    }
}
