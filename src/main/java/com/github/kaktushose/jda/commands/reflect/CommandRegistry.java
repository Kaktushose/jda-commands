package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.plugins.CommandPlugin;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Central registry for all {@link CommandDefinition CommandDefinitions}.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @since 2.0.0
 */
public class CommandRegistry {

    private final static Logger log = LoggerFactory.getLogger(CommandRegistry.class);
    private final TypeAdapterRegistry parameterRegistry;
    private final ValidatorRegistry validatorRegistry;
    private final DependencyInjector dependencyInjector;
    private final Set<ControllerDefinition> controllers;
    private final Set<CommandDefinition> commands;

    /**
     * Constructs a new CommandRegistry.
     *
     * @param adapterRegistry    the corresponding {@link TypeAdapterRegistry}
     * @param validatorRegistry  the corresponding {@link ValidatorRegistry}
     * @param dependencyInjector the corresponding {@link DependencyInjector}
     */
    public CommandRegistry(@NotNull TypeAdapterRegistry adapterRegistry,
                           @NotNull ValidatorRegistry validatorRegistry,
                           @NotNull DependencyInjector dependencyInjector) {
        this.parameterRegistry = adapterRegistry;
        this.validatorRegistry = validatorRegistry;
        this.dependencyInjector = dependencyInjector;
        controllers = new HashSet<>();
        commands = new HashSet<>();
    }

    /**
     * Scans the whole classpath for commands.
     *
     * @param packages package(s) to exclusively scan
     * @param clazz    a class of the classpath to scan
     */
    public void index(@NotNull Class<?> clazz, String pluginDir, @NotNull String... packages) {
        log.debug("Indexing controllers...");

        ConfigurationBuilder config = new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner())
                .setUrls(ClasspathHelper.forClass(clazz))
                .filterInputsBy(new FilterBuilder().includePackage(packages));
        Reflections reflections = new Reflections(config);

        Set<Class<?>> pluginControllers = indexPlugins(pluginDir);

        Set<Class<?>> controllerSet = reflections.getTypesAnnotatedWith(CommandController.class);

        controllerSet.addAll(pluginControllers);

        for (Class<?> aClass : controllerSet) {
            log.debug("Found controller {}", aClass.getName());

            Optional<ControllerDefinition> optional = ControllerDefinition.build(aClass,
                    parameterRegistry,
                    validatorRegistry,
                    dependencyInjector
            );

            if (!optional.isPresent()) {
                log.warn("Unable to index the controller!");
                continue;
            }

            ControllerDefinition controller = optional.get();
            controllers.add(controller);
            commands.addAll(controller.getSuperCommands());
            commands.addAll(controller.getSubCommands());

            log.debug("Registered controller {}", controller);
        }

        log.debug("Successfully registered {} controller(s) with a total of {} command(s)!", controllers.size(), commands.size());
    }

    /**
     * Scans the given directory for plugins and returns all controllers found in the plugins.
     * @param pluginDir the directory to scan for plugins
     * @return a set of all {@link CommandController CommandControllers} found in the plugins
     */
    private Set<Class<?>> indexPlugins(String pluginDir) {

        // Check if the pluginDir is valid
        if (pluginDir == null) {
            log.debug("No plugin directory specified. Skipping plugin indexing...");
            return Collections.emptySet();
        }

        File pluginFolder = new File(pluginDir);
        if (!pluginFolder.exists()) {
            if (!pluginFolder.mkdirs()) {
                log.warn("Unable to create plugin directory. Skipping plugin indexing...");
                return Collections.emptySet();
            }
        }


        // Get all files in the plugin directory with the .jar extension
        File[] files = pluginFolder.listFiles((file) -> file.getName().endsWith(".jar"));

        // If there are no files, return an empty set
        if (files == null) {
            log.debug("No plugins found. Skipping plugin indexing...");
            return Collections.emptySet();
        }

        // Create a lists of Jar Files and URLs
        List<JarFile> jars = new ArrayList<>();
        List<URL> urls = new ArrayList<>();
        for (File file : files) {
            try {
                jars.add(new JarFile(file));
                urls.add(file.toURI().toURL());
            } catch (IOException e) {
                log.warn("Unable to load plugin jar file: {}", file.getName());
            }
        }

        // Create a new class loader with the URLs to the Jar Files
        URLClassLoader loaders = new URLClassLoader(urls.toArray(new URL[0]));

        Set<Class<? extends CommandPlugin>> pluginClasses = new HashSet<>();
        for (JarFile jar : jars) {
            Enumeration<JarEntry> entries = jar.entries();
            // Find all classes by iterating over all entries in the jar file
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName().replace("/", ".").substring(0, entry.getName().length() - 6);
                    try {
                        // Load the class and check if it is a CommandPlugin
                        Class<?> clazz = loaders.loadClass(className);
                        if (CommandPlugin.class.isAssignableFrom(clazz)) {
                            //noinspection unchecked - we know that the class is a subclass of CommandPlugin because of the isAssignableFrom check
                            pluginClasses.add((Class<? extends CommandPlugin>) clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        log.warn("Unable to load plugin class: {}", className);
                    }
                }
            }
        }

        //Create a new instance of each plugin and get the Command Packages from it.
        List<String> packages = new ArrayList<>();
        pluginClasses.forEach(plugin -> {
            try {
                CommandPlugin commandPlugin = (CommandPlugin) plugin.getConstructors()[0].newInstance();
                packages.addAll(commandPlugin.getCommandPackages());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                log.warn("Unable to load plugin: {}", plugin.getName());
            }
        });

        // Scan the packages for CommandControllers
        ConfigurationBuilder config = new ConfigurationBuilder()
                .addClassLoaders(loaders)
                .setUrls(ClasspathHelper.forClassLoader(loaders))
                .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner())
                .filterInputsBy(new FilterBuilder().includePackage(packages.toArray(new String[0])));

        Reflections pluginReflections = new Reflections(config);
        
        return pluginReflections.getTypesAnnotatedWith(CommandController.class);
    }

    /**
     * Gets a list of all {@link ControllerDefinition ControllerDefinitions}.
     *
     * @return a list of all {@link ControllerDefinition ControllerDefinitions}
     */
    public Set<ControllerDefinition> getControllers() {
        return Collections.unmodifiableSet(controllers);
    }

    /**
     * Gets a list of all {@link CommandDefinition CommandDefinitions}.
     *
     * @return a list of all {@link CommandDefinition CommandDefinitions}
     */
    public Set<CommandDefinition> getCommands() {
        return Collections.unmodifiableSet(commands);
    }
}
