package com.github.kaktushose.jda.commands.internal;

import com.github.kaktushose.jda.commands.annotations.*;
import com.github.kaktushose.jda.commands.entities.CommandCallable;
import com.github.kaktushose.jda.commands.entities.CommandEvent;
import com.github.kaktushose.jda.commands.entities.Parameter;
import com.github.kaktushose.jda.commands.exceptions.CommandException;
import com.google.common.collect.Sets;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.*;

final class CommandRegistry {

    private static final Logger log = LoggerFactory.getLogger(CommandRegistry.class);
    private final Set<CommandCallable> commands;
    private final DependencyInjector dependencyInjector;

    CommandRegistry(DependencyInjector dependencyInjector) {
        this.commands = new HashSet<>();
        this.dependencyInjector = dependencyInjector;
    }

    final Set<CommandCallable> getCommands() {
        return commands;
    }

    void indexCommandController(String packageName) {
        log.debug("Indexing commands...");

        Reflections reflections;
        if (packageName == null) {
            log.debug("No package specified. Going to scan whole project...");
            reflections = new Reflections("");
        } else {
            log.debug("Going to scan package '{}'...", packageName);
            reflections = new Reflections(packageName);
        }


        Set<Class<?>> controllerSet = reflections.getTypesAnnotatedWith(CommandController.class);
        for (Class<?> controllerClass : controllerSet) {
            log.debug("Found CommandController {}", controllerClass.getName());
            indexController(controllerClass);
        }
        log.info("Command indexing done! Indexed a total of {} commands!", commands.size());
    }

    private void indexController(Class<?> controllerClass) {
        // validating CommandController
        CommandController commandController = controllerClass.getAnnotation(CommandController.class);
        if (!commandController.isActive()) {
            log.warn("CommandController {} is set inactive. Skipping the controller and its commands", controllerClass.getName());
            return;
        }
        // create instance of class
        Object instance;
        try {
            instance = controllerClass.getConstructors()[0].newInstance();
        } catch (Exception e) {
            log.error("Unable to create controller instance!", e);
            return;
        }
        // index fields for dependency injection
        indexInjectableFields(controllerClass, instance);

        // indexing each Command
        for (Method method : controllerClass.getDeclaredMethods()) {
            log.debug("Found command {}", method.getName());

            if (!method.isAnnotationPresent(Command.class)) {
                continue;
            }
            if (!method.getReturnType().equals(Void.TYPE) || !Modifier.isPublic(method.getModifiers())) {
                logError("Command method has an invalid return type or access modifier!", method);
                continue;
            }

            indexCommand(method, commandController, new HashSet<>(indexControllerPermissions(controllerClass)), instance);
        }
    }

    private void indexInjectableFields(Class<?> controllerClass, Object instance) {
        // index dependency injection fields
        List<Field> fields = new ArrayList<>();
        for (Field field : controllerClass.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Inject.class)) {
                continue;
            }
            fields.add(field);
        }
        dependencyInjector.addDependency(instance, fields);
    }

    private Set<String> indexControllerPermissions(Class<?> controllerClass) {
        if (controllerClass.isAnnotationPresent(Permission.class)) {
            Permission permission = controllerClass.getAnnotation(Permission.class);
            return Sets.newHashSet(permission.value());
        }
        return Collections.emptySet();
    }

    private void indexCommand(Method method, CommandController commandController, Set<String> permissions, Object instance) {
        Command command = method.getAnnotation(Command.class);
        if (!command.isActive()) {
            log.warn("Command {} is set inactive. Skipping this command", method.getName());
            return;
        }

        // index command permissions
        permissions.addAll(indexCommandPermissions(method));
        // creating all possible labels for a command
        List<String> labels = generateLabels(command, commandController);

        // validating method signature
        Optional<List<Parameter>> optional = indexParameters(method);
        if (!optional.isPresent()) {
            return;
        }
        List<Parameter> parameters = optional.get();

        // this must change if command overloading is implemented
        if (commands.stream().anyMatch(commandCallable -> commandCallable.getLabels().stream().anyMatch(labels::contains))) {
            logError("The labels for the command are already registered!", method);
            return;
        }

        String category = commandController.category();
        if (!command.category().equals("Other")) {
            category = command.category();
        }

        CommandCallable commandCallable = new CommandCallable(labels,
                command.name(),
                command.desc(),
                command.usage(),
                category,
                parameters,
                permissions,
                method,
                instance);
        commands.add(commandCallable);
        log.debug("Registered command {}", commandCallable);

    }

    private Set<String> indexCommandPermissions(Method method) {
        if (method.isAnnotationPresent(Permission.class)) {
            Permission permission = method.getAnnotation(Permission.class);
            return Sets.newHashSet(permission.value());
        }
        return Collections.emptySet();
    }

    private List<String> generateLabels(Command command, CommandController commandController) {
        List<String> labels = new ArrayList<>();
        for (String controllerLabel : commandController.value()) {
            for (String commandLabel : command.value()) {
                String label = (controllerLabel + " " + commandLabel).trim();
                labels.add(label);
            }
        }
        return labels;
    }

    private Optional<List<Parameter>> indexParameters(Method method) {
        List<Parameter> parameters = new ArrayList<>();
        boolean hasOptional = false; // if true the checked parameter must be optional

        AnnotatedType[] parameterTypes = method.getAnnotatedParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            AnnotatedType parameterType = parameterTypes[i];
            String name = ParameterType.wrap(parameterType.getType().getTypeName());
            // first method argument is not a CommandEvent
            if (i == 0) {
                if (!name.equals(CommandEvent.class.getName())) {
                    logError(String.format("Command method has an invalid method signature! First parameter must be of type %s!",
                            CommandEvent.class.getName()), method);
                    return Optional.empty();
                }
                continue; // first parameter is of type CommandEvent, continue with second parameter
            }

            // check if parameter type is supported
            if (!ParameterType.isValid(name)) {
                logError(String.format("Command method has an invalid method signature! %s is an unsupported method parameter!",
                        name), method);
                return Optional.empty();
            }

            // check if the parameter is an array. If true argument parsing isn't needed, because the raw input will
            // be passed to the method as an String array
            if (name.equals(ParameterType.ARRAY.name)) {
                if (parameterTypes.length > 2) {
                    logError("Command method has an invalid method signature! Parameters aren't allowed when using arrays!", method);
                    return Optional.empty();
                }
                parameters.add(new Parameter(false, false, "", ParameterType.ARRAY.name));
                return Optional.of(parameters);
            }

            // check for parameter annotations
            boolean isConcat = false;
            boolean isOptional = false;
            int optionalIndex = -1;
            String defaultValue = "";
            for (Annotation parameterAnnotation : method.getParameterAnnotations()[i]) {
                Class<? extends Annotation> annotationClass = parameterAnnotation.annotationType();

                // check if String concatenation is enabled
                if (annotationClass.equals(Concat.class)) {
                    // must be last parameter
                    if (i != parameterTypes.length - 1) {
                        logError("Command method has an invalid method signature! " +
                                "Concatenation may be only enabled for the last parameter", method);
                        return Optional.empty();
                    }
                    // must be a String
                    if (!name.equals("java.lang.String")) {
                        logError("Command method has an invalid method signature! " +
                                "Concatenation is enabled but last parameter is not a String!", method);
                        return Optional.empty();
                    }
                    isConcat = true;
                }

                // if method already had an optional parameter (hasOptional == true) this one has to be optional as well
                if (hasOptional && optionalIndex != i && !annotationClass.equals(com.github.kaktushose.jda.commands.annotations.Optional.class)) {
                    logError("Command method has an invalid method signature! " +
                            "An optional parameter must not be followed by a non-optional parameter!", method);
                    return Optional.empty();
                }
                // check if it's an optional parameter and get the default value
                if (annotationClass.equals(com.github.kaktushose.jda.commands.annotations.Optional.class)) {
                    isOptional = true;
                    hasOptional = true;
                    optionalIndex = i;
                    defaultValue = ((com.github.kaktushose.jda.commands.annotations.Optional) parameterAnnotation).value();
                    if (defaultValue.equals("") && method.getParameterTypes()[i].isPrimitive()) {
                        log.warn("Command {} has an optional primitive datatype parameter, but no default value is present!" +
                                "This will result in a NullPointerException if the command is executed without the optional parameter!", method.getName());
                    }
                }
            }

            parameters.add(new Parameter(isConcat, isOptional, defaultValue, name));
        }

        return Optional.of(parameters);
    }

    private void logError(String message, Method commandMethod) {
        log.error("An error has occurred! Skipping Command {}!",
                commandMethod.getName(),
                new CommandException(message));
    }

}
