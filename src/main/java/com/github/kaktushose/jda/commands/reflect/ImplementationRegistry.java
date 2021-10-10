package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.Component;
import com.github.kaktushose.jda.commands.embeds.DefaultHelpMessageFactory;
import com.github.kaktushose.jda.commands.embeds.HelpMessageFactory;
import com.github.kaktushose.jda.commands.permissions.DefaultPermissionsProvider;
import com.github.kaktushose.jda.commands.permissions.PermissionsProvider;
import com.github.kaktushose.jda.commands.settings.DefaultSettingsProvider;
import com.github.kaktushose.jda.commands.settings.SettingsProvider;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.Set;

public class ImplementationRegistry {

    private static final Logger log = LoggerFactory.getLogger(ImplementationRegistry.class);
    private static Reflections reflections;
    private SettingsProvider settingsProvider;
    private PermissionsProvider permissionsProvider;
    private HelpMessageFactory helpMessageFactory;

    public ImplementationRegistry() {
        settingsProvider = new DefaultSettingsProvider();
        permissionsProvider = new DefaultPermissionsProvider();
        helpMessageFactory = new DefaultHelpMessageFactory();
    }

    public void index(String... packages) {
        log.debug("Indexing custom implementations...");
        ConfigurationBuilder config = new ConfigurationBuilder()
                .setScanners(new SubTypesScanner())
                .setUrls(ClasspathHelper.forClass(getClass()))
                .filterInputsBy(new FilterBuilder().includePackage(packages));
        reflections = new Reflections(config);

        findImplementation(SettingsProvider.class).ifPresent(this::setSettingsProvider);

        findImplementation(PermissionsProvider.class).ifPresent(this::setPermissionsProvider);

        findImplementation(HelpMessageFactory.class).ifPresent(this::setHelpMessageFactory);
    }

    public SettingsProvider getSettingsProvider() {
        return settingsProvider;
    }

    public void setSettingsProvider(SettingsProvider settingsProvider) {
        this.settingsProvider = settingsProvider;
    }

    public PermissionsProvider getPermissionsProvider() {
        return permissionsProvider;
    }

    public void setPermissionsProvider(PermissionsProvider permissionsProvider) {
        this.permissionsProvider = permissionsProvider;
    }

    public HelpMessageFactory getHelpMessageFactory() {
        return helpMessageFactory;
    }

    public void setHelpMessageFactory(HelpMessageFactory helpMessageFactory) {
        this.helpMessageFactory = helpMessageFactory;
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<T> findImplementation(Class<T> type) {
        Set<Class<? extends T>> implementations = reflections.getSubTypesOf(type);
        for (Class<?> clazz : implementations) {
            if (!clazz.isAnnotationPresent(Component.class)) {
                continue;
            }
            log.debug("Found {}", clazz.getName());
            try {
                return Optional.of((T) clazz.getConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.error("Unable to create an instance of the custom implementation!", e);

            }
        }
        return Optional.empty();
    }
}
